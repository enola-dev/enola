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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.ai.mcp.McpServerConnectionsConfig.ServerConnection;
import dev.enola.common.Version;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.jackson.JacksonObjectReaderWriterChain;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.name.NamedTypedObjectProvider;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpTransport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class McpLoader implements NamedTypedObjectProvider<McpSyncClient> {

    private static final Logger LOG = LoggerFactory.getLogger(McpLoader.class);

    private final ObjectReader objectReader = new JacksonObjectReaderWriterChain();
    private final Map<String, McpServerConnectionsConfig> configs = new MapMaker().makeMap();
    private final Map<String, McpSyncClient> clients = new MapMaker().makeMap();
    private final Queue<String> names = new ConcurrentLinkedQueue<>();

    @CanIgnoreReturnValue
    public McpServerConnectionsConfig load(ReadableResource resource) throws IOException {
        var config = objectReader.read(resource, McpServerConnectionsConfig.class);
        config.origin = resource.uri();
        var serverNames = config.servers.keySet();
        names.addAll(serverNames);
        for (var name : serverNames) configs.put(name, config);
        return config;
    }

    public Iterable<McpServerConnectionsConfig> configs() {
        return configs.values();
    }

    @Override
    public Iterable<String> names() {
        return names;
    }

    @Override
    public Optional<McpSyncClient> opt(String name) {
        var config = configs.get(name);
        if (config == null) return Optional.empty();

        return Optional.of(clients.computeIfAbsent(name, k -> createSyncClient(config, name)));
    }

    // TODO Remove this method again, if both dev.enola.ai.adk.tool.ADK and McpLoaderTest don't use?
    // TODO create MCP transports in parallel, and asynchronously?
    private Map<String, McpTransport> createTransports(
            Iterable<McpServerConnectionsConfig> configs) {
        var transports = new ImmutableMap.Builder<String, McpTransport>();
        for (var config : configs) {
            for (var name : config.servers.keySet()) {
                var transport = createTransport(config, name);
                transports.put(name, transport);
            }
        }
        return transports.build();
    }

    public static McpClientTransport createTransport(
            McpServerConnectionsConfig config, String name) {
        McpClientTransport transport;
        URI origin = config.origin;
        ServerConnection connectionConfig = config.servers.get(name);
        switch (connectionConfig.type) {
            case stdio -> {
                var params = createStdIoServerParameters(connectionConfig);
                transport = new StdioClientTransport(params);
            }
            case http -> {
                transport = createHttpClientSseClientTransport(connectionConfig);
            }
            default ->
                    throw new IllegalArgumentException(
                            "%s#%s: Unknown MCP transport type: %s"
                                    .formatted(origin, name, connectionConfig.type));
        }
        return transport;
    }

    private static McpSyncClient createSyncClient(McpServerConnectionsConfig config, String name) {
        var origin = config.origin + "#" + name;
        var transport = createTransport(config, name);
        return createSyncClient(transport, origin);
    }

    private static McpSyncClient createSyncClient(McpClientTransport transport, String origin) {
        var implementation = new McpSchema.Implementation("https://Enola.dev", Version.get());
        var client =
                McpClient.sync(transport)
                        .clientInfo(implementation)
                        .capabilities(McpSchema.ClientCapabilities.builder().build())
                        // TODO Make this configurable - but how & from where?
                        // .initializationTimeout(Duration.ofSeconds(7))
                        // .requestTimeout(Duration.ofSeconds(7))
                        .loggingConsumer(new McpServer(origin))
                        .build();
        client.initialize();
        client.ping();
        var serverInfo = client.getServerInfo();
        LOG.info("{} initialized: {} @ {}", origin, serverInfo.name(), serverInfo.version());
        return client;
    }

    // TODO Inline (again) into above?

    private static ServerParameters createStdIoServerParameters(ServerConnection connectionConfig) {
        if (connectionConfig.type != stdio) throw new IllegalArgumentException();
        return ServerParameters.builder(connectionConfig.command)
                .args(connectionConfig.args)
                // TODO Process env to substitute secrets and $HOME etc.
                .env(connectionConfig.env)
                .build();
    }

    private static HttpClientSseClientTransport createHttpClientSseClientTransport(
            ServerConnection connectionConfig) {
        if (connectionConfig.type != http) throw new IllegalArgumentException();
        // TODO Set headers, timeout etc.
        return HttpClientSseClientTransport.builder(connectionConfig.url).build();
    }
}
