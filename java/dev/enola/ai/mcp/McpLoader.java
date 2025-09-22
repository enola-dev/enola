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
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class McpLoader implements NamedTypedObjectProvider<McpSyncClient> {

    // TODO Also support McpAsyncClient

    // TODO Replace the (not really required) concurrency support with a Builder with load()

    private static final Logger LOG = LoggerFactory.getLogger(McpLoader.class);

    private final ObjectReader objectReader = new JacksonObjectReaderWriterChain();
    private final Map<String, McpServerConnectionsConfig> serverToConfig = new MapMaker().makeMap();
    private final Map<String, McpSyncClient> clients = new MapMaker().makeMap();
    private final SecretManager secretManager;

    public McpLoader(SecretManager secretManager) {
        this.secretManager = secretManager;
    }

    @CanIgnoreReturnValue
    public McpServerConnectionsConfig load(ReadableResource resource) throws IOException {
        var config = objectReader.read(resource, McpServerConnectionsConfig.class);
        config.origin = resource.uri();

        for (var serverConnection : config.servers.values()) {
            replaceSecretPlaceholders(serverConnection.env);
        }

        var serverNames = config.servers.keySet();
        for (var name : serverNames) serverToConfig.put(name, config);

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

        return Optional.of(clients.computeIfAbsent(name, k -> createSyncClient(config, name)));
    }

    private McpClientTransport createTransport(McpServerConnectionsConfig config, String name) {
        var origin = config.origin + "#" + name;
        ServerConnection connectionConfig = config.servers.get(name);
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

    private McpSyncClient createSyncClient(McpServerConnectionsConfig config, String name) {
        var origin = config.origin + "#" + name;
        var transport = createTransport(config, name);
        var withRoots = config.servers.get(name).roots;
        var logLevel = config.servers.get(name).log;
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

    private void replaceSecretPlaceholders(Map<String, String> env) throws IOException {
        if (env.isEmpty()) {
            return;
        }

        final String prefix = "${secret:";
        final String suffix = "}";

        for (Map.Entry<String, String> entry : env.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value.startsWith(prefix) && value.endsWith(suffix)) {
                var secretName = value.substring(prefix.length(), value.length() - suffix.length());
                var secret = secretManager.get(secretName);
                env.put(key, secret.map(String::new));
            }
        }
    }
}
