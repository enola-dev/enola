/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
import com.google.common.collect.MapMaker;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.ai.mcp.McpLoader;
import dev.enola.common.jackson.ObjectMappers;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@ThreadSafe
class MCP implements ToolsetProvider {

    private final McpLoader mcpLoader;
    private final ConcurrentMap<String, McpToolset> toolsetMap = new MapMaker().makeMap();

    MCP(McpLoader mcpLoader) {
        this.mcpLoader = mcpLoader;
    }

    @Override
    public Set<String> names() {
        return mcpLoader.names();
    }

    @Override
    public Optional<BaseToolset> opt(String name) {
        if (!names().contains(name)) return Optional.empty();
        else return Optional.of(toolsetMap.computeIfAbsent(name, k -> createToolset(name)));
    }

    private McpToolset createToolset(String name) {
        var mcpSessionManager = new MyMcpSessionManager(name, mcpLoader);
        var toolFilter = Optional.empty();
        var adkObjectMapper = JsonBaseModel.getMapper();
        var newObjectMapper = adkObjectMapper.copy();
        ObjectMappers.configure(newObjectMapper);
        return new McpToolset(mcpSessionManager, newObjectMapper, toolFilter);
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
