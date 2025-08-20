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
package dev.enola.ai.adk.tool;

import com.google.adk.JsonBaseModel;
import com.google.adk.tools.BaseToolset;
import com.google.adk.tools.mcp.McpSessionManager;
import com.google.adk.tools.mcp.McpToolset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.ai.mcp.McpLoader;
import dev.enola.ai.mcp.McpServerConnectionsConfig;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@ThreadSafe
class MCP implements ToolsetProvider {

    private final McpLoader mcpLoader;
    private final ImmutableMap<String, McpServerConnectionsConfig> configMap;
    private final ConcurrentMap<String, McpToolset> toolsetMap = new MapMaker().makeMap();

    MCP(Iterable<McpServerConnectionsConfig> configs, McpLoader mcpLoader) {
        this.mcpLoader = mcpLoader;
        var mapBuilder = ImmutableMap.<String, McpServerConnectionsConfig>builder();
        for (var config : configs) {
            for (var name : config.servers.keySet()) {
                mapBuilder.put(name, config);
            }
        }
        this.configMap = mapBuilder.buildOrThrow();
    }

    @Override
    public Iterable<String> names() {
        return mcpLoader.names();
    }

    @Override
    public Optional<BaseToolset> opt(String name) {
        var config = configMap.get(name);
        if (config == null) return Optional.empty();
        else return Optional.of(toolsetMap.computeIfAbsent(name, k -> createToolset(name)));
    }

    private McpToolset createToolset(String name) {
        var mcpSessionManager = new MyMcpSessionManager(name, mcpLoader);
        var toolFilter = Optional.empty();
        var objectMapper = JsonBaseModel.getMapper(); // TODO Use Enola's own instead?!
        return new McpToolset(mcpSessionManager, objectMapper, toolFilter);
    }

    private static final class MyMcpSessionManager extends McpSessionManager {

        private final String name;
        private final McpLoader mcpLoader;

        MyMcpSessionManager(String name, McpLoader mcpLoader) {
            super(null, null);
            this.mcpLoader = mcpLoader;
            this.name = name;
        }

        @Override
        public McpSyncClient createSession() {
            return mcpLoader.get(name, mcpLoader);
        }

        @Override
        public McpAsyncClient createAsyncSession() {
            throw new UnsupportedOperationException(
                    "TODO Implement McpAsyncClient support in McpLoader");
        }
    }
}
