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

import dev.enola.ai.mcp.McpServerConnectionsConfig.ServerConnection;
import dev.enola.common.Version;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.jackson.JacksonObjectReaderWriterChain;
import dev.enola.common.io.resource.ReadableResource;

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

public class McpLoader {

    private static final Logger LOG = LoggerFactory.getLogger(McpLoader.class);

    private final ObjectReader objectReader = new JacksonObjectReaderWriterChain();

    public McpServerConnectionsConfig load(ReadableResource resource) throws IOException {
        var config = objectReader.read(resource, McpServerConnectionsConfig.class);
        config.origin = resource.uri();
        return config;
    }

    // TODO Remove this method again, if both dev.enola.ai.adk.tool.ADK and McpLoaderTest don't use?
    // TODO create MCP transports in parallel, and asynchronously?
    public Map<String, McpTransport> createTransports(McpServerConnectionsConfig config) {
        var transports = new ImmutableMap.Builder<String, McpTransport>();
        for (var name : config.servers.keySet()) {
            var transport = createTransport(config, name);
            transports.put(name, transport);
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

    public static McpSyncClient createSyncClient(McpServerConnectionsConfig config, String name) {
        var origin = config.origin + "#" + name;
        var transport = createTransport(config, name);
        return createSyncClient(transport, origin);
    }

    public static McpSyncClient createSyncClient(McpClientTransport transport, String origin) {
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
