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

import static dev.enola.ai.mcp.McpServerConnectionsConfig.ServerConnection.Type.http;
import static dev.enola.ai.mcp.McpServerConnectionsConfig.ServerConnection.Type.stdio;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

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
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
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

    @CanIgnoreReturnValue // TODO Separate public from testing API more clearly
    public McpServerConnectionsConfig load(ReadableResource resource) throws IOException {
        var config = objectReader.read(resource, McpServerConnectionsConfig.class);
        config.origin = resource.uri();

        var serverNames = config.servers.keySet();
        for (var name : serverNames) {
            var serverConnectionConfig = config.servers.get(name);
            if (serverConnectionConfig.origin != null)
                throw new IOException("Use homepage: instead of origin: in " + config.origin);
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

    @Override
    public Set<String> names() {
        return serverToConfig.keySet();
    }

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
        switch (connectionConfig.type) {
            case stdio -> {
                var params = createStdIoServerParameters(connectionConfig);
                var transport = new StdioClientTransport(params);
                transport.setStdErrorHandler(new McpServerStdErrLogConsumer(origin));
                return transport;
            }
            case http -> {
                return createHttpClientSseClientTransport(connectionConfig);
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
                        // TODO Make this configurable - but how & from where?
                        // .initializationTimeout(Duration.ofSeconds(7))
                        // .requestTimeout(Duration.ofSeconds(7))
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

    // TODO Inline (again) into above?

    private ServerParameters createStdIoServerParameters(ServerConnection connectionConfig) {
        if (connectionConfig.type != stdio) throw new IllegalArgumentException();
        return ServerParameters.builder(connectionConfig.command)
                .args(connectionConfig.args)
                .env(connectionConfig.env)
                .build();
    }

    private HttpClientSseClientTransport createHttpClientSseClientTransport(
            ServerConnection connectionConfig) {
        if (connectionConfig.type != http) throw new IllegalArgumentException();
        // TODO Set headers, timeout etc.
        return HttpClientSseClientTransport.builder(connectionConfig.url).build();
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
        final String prefix = "${secret:";
        final String suffix = "}";
        if (!value.startsWith(prefix) || !value.endsWith(suffix)) return value;

        var nameStartIndex = prefix.length();
        var nameEndIndex = value.length() - suffix.length();
        if (nameStartIndex >= nameEndIndex)
            throw new IOException("Invalid secret placeholder: " + value);
        var secretName = value.substring(nameStartIndex, nameEndIndex);

        return secretManager
                .getOptional(secretName)
                .map(secret -> secret.map(String::new))
                .orElseThrow(() -> new IOException("Secret not found: " + value));
    }
}
