/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.enola.ai.mcp;

import static dev.enola.ai.mcp.McpServerConnectionsConfig.ServerConnection.Type.*;
import static dev.enola.ai.mcp.McpServerConnectionsConfig.ServerConnection.Type.http;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

import dev.enola.ai.mcp.McpServerConnectionsConfig.ServerConnection;
import dev.enola.common.Version;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.jackson.JacksonObjectReaderWriterChain;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.name.NamedTypedObjectProvider;
import dev.enola.common.secret.SecretManager;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class McpLoader implements NamedTypedObjectProvider<McpSyncClient> {

    // TODO Also support McpAsyncClient

    private static final Logger LOG = LoggerFactory.getLogger(McpLoader.class);

    private final ObjectReader objectReader = new JacksonObjectReaderWriterChain();
    private final Map<String, McpServerConnectionsConfig.ServerConnection> serverToConfig =
            new MapMaker().makeMap();
    private final Map<String, McpSyncClient> clients = new MapMaker().makeMap();
    private final SecretManager secretManager;

    public McpLoader(SecretManager secretManager) {
        this.secretManager = secretManager;
    }

    public void load(ReadableResource resource) throws IOException {
        loadAndReturn(resource);
    }

    @VisibleForTesting
    McpServerConnectionsConfig loadAndReturn(ReadableResource resource) throws IOException {
        var config = objectReader.read(resource, McpServerConnectionsConfig.class);
        config.origin = resource.uri();

        var serverNames = config.servers.keySet();
        for (var name : serverNames) {
            var serverConnectionConfig = config.servers.get(name);
            if (serverConnectionConfig.origin != null)
                throw new IOException(
                        "The 'origin' field is reserved; use 'docs' for the documentation URL in "
                                + config.origin);
            serverConnectionConfig.origin = URI.create(config.origin + "#" + name);
            var previousConfig = serverToConfig.putIfAbsent(name, serverConnectionConfig);
            if (previousConfig != null) {
                throw new IOException(
                        "Duplicate MCP server name '"
                                + name
                                + "' from "
                                + config.origin
                                + ", already defined in "
                                + previousConfig.origin);
            }
        }
        return config;
    }

    /** List available MCP tool names. */
    @Override
    public Set<String> names() {
        return serverToConfig.keySet();
    }

    /**
     * Get an (optional) MCP tool by name.
     *
     * <p>Note that it's possible that this returns {@link Optional#empty()} for a name returned by
     * {@link #names()} in case the tool failed to load, including due to a missing secret.
     */
    @Override
    public Optional<McpSyncClient> opt(String name) {
        LOG.info("Get (optional) {}", name);
        var config = serverToConfig.get(name);
        if (config == null) return Optional.empty();

        try {
            var config2 = replaceSecretPlaceholders(config);
            return Optional.of(clients.computeIfAbsent(name, k -> createSyncClient(config2)));
        } catch (IOException e) {
            LOG.error("Exception during Secret retrieval: {}", config.origin, e);
            return Optional.empty();
        }
    }

    @VisibleForTesting
    McpServerConnectionsConfig.ServerConnection replaceSecretPlaceholders(
            McpServerConnectionsConfig.ServerConnection originalServerConnection)
            throws IOException {
        var newServerConnection = new ServerConnection(originalServerConnection);
        newServerConnection.args = replaceSecretPlaceholders(originalServerConnection.args);
        newServerConnection.env = replaceSecretPlaceholders(originalServerConnection.env);
        newServerConnection.headers = replaceSecretPlaceholders(originalServerConnection.headers);
        return newServerConnection;
    }

    private McpClientTransport createTransport(
            McpServerConnectionsConfig.ServerConnection connectionConfig) {
        var origin = connectionConfig.origin.toString();
        if (!Strings.isNullOrEmpty(connectionConfig.url) && !sse.equals(connectionConfig.type))
            connectionConfig.type = http;
        switch (connectionConfig.type) {
            case stdio -> {
                var params = createStdIoServerParameters(connectionConfig);
                var transport = new StdioClientTransport(params);
                transport.setStdErrorHandler(new McpServerStdErrLogConsumer(origin));
                return transport;
            }
            case http, sse -> {
                return createHttpTransport(connectionConfig);
            }
            default ->
                    throw new IllegalArgumentException(
                            "%s: Unknown MCP transport type: %s"
                                    .formatted(origin, connectionConfig.type));
        }
    }

    private McpSyncClient createSyncClient(McpServerConnectionsConfig.ServerConnection config) {
        var origin = config.origin.toString();
        var transport = createTransport(config);
        var withRoots = config.roots;
        var logLevel = config.log;
        var implementation = new McpSchema.Implementation("https://Enola.dev", Version.get());
        var capabilities = McpSchema.ClientCapabilities.builder();
        if (withRoots) capabilities.roots(false);
        var client =
                McpClient.sync(transport)
                        .clientInfo(implementation)
                        .capabilities(capabilities.build())
                        .initializationTimeout(config.timeout)
                        .requestTimeout(config.timeout)
                        .loggingConsumer(new McpServerLogConsumer(origin))
                        .build();

        var initResult = client.initialize();
        LOG.info("{} initializing: {}", origin, initResult);

        // To avoid "Method not found", check logging capability before setting it
        if (client.getServerCapabilities().logging() != null) client.setLoggingLevel(logLevel);

        if (withRoots) {
            // TODO Allow adding several roots?
            // TODO Allow configuring root(s) other than the current directory
            var cwd = Paths.get("").toUri().toString();
            var root = new McpSchema.Root(cwd, null);
            client.addRoot(root);
        }

        client.ping();

        var serverInfo = client.getServerInfo();
        LOG.info("{} fully initialized: {} @ {}", origin, serverInfo.name(), serverInfo.version());
        return client;
    }

    private ServerParameters createStdIoServerParameters(ServerConnection connectionConfig) {
        return ServerParameters.builder(connectionConfig.command)
                .args(connectionConfig.args)
                .env(connectionConfig.env)
                .build();
    }

    private McpClientTransport createHttpTransport(ServerConnection connectionConfig) {
        var requestBuilder = HttpRequest.newBuilder();
        connectionConfig.headers.forEach(requestBuilder::header);
        if (connectionConfig.type == http) {
            var transportBuilder = HttpClientStreamableHttpTransport.builder(connectionConfig.url);
            transportBuilder.requestBuilder(requestBuilder);
            transportBuilder.connectTimeout(connectionConfig.timeout);
            return transportBuilder.build();
        } else { // SSE
            var transportBuilder = HttpClientSseClientTransport.builder(connectionConfig.url);
            transportBuilder.requestBuilder(requestBuilder);
            transportBuilder.connectTimeout(connectionConfig.timeout);
            return transportBuilder.build();
        }
    }

    private Map<String, String> replaceSecretPlaceholders(Map<String, String> map)
            throws IOException {
        if (map.isEmpty()) {
            return Map.of();
        }
        var mapBuilder = ImmutableMap.<String, String>builderWithExpectedSize(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapBuilder.put(entry.getKey(), replaceSecretPlaceholders(entry.getValue()));
        }
        return mapBuilder.build();
    }

    private List<String> replaceSecretPlaceholders(List<String> list) throws IOException {
        if (list.isEmpty()) {
            return List.of();
        }
        var listBuilder = ImmutableList.<String>builderWithExpectedSize(list.size());
        for (String value : list) {
            listBuilder.add(replaceSecretPlaceholders(value));
        }
        return listBuilder.build();
    }

    private String replaceSecretPlaceholders(String value) throws IOException {
        final var prefix = "${secret:";
        final var suffix = '}';
        if (value.contains("${") && !value.contains(prefix))
            throw new IOException("Invalid secret placeholder; must be ${secret:XYZ}: " + value);
        if (!value.contains(prefix)) return value;

        int placeholderStart = value.indexOf(prefix);
        if (placeholderStart == -1) {
            return value;
        }

        int nameEnd = value.indexOf(suffix, placeholderStart);
        if (nameEnd == -1) {
            throw new IOException("Invalid secret placeholder, missing " + suffix + ": " + value);
        }

        int nameStart = placeholderStart + prefix.length();
        var secretName = value.substring(nameStart, nameEnd);

        var secretValue =
                secretManager
                        .getOptional(secretName)
                        .map(secretOpt -> secretOpt.map(String::new))
                        .orElseThrow(() -> new IOException("Secret not found: " + secretName));

        return value.substring(0, placeholderStart) + secretValue + value.substring(nameEnd + 1);
    }
}
