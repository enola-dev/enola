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
import com.google.adk.tools.mcp.McpSessionManager;
import com.google.adk.tools.mcp.McpToolset;
import com.google.adk.tools.mcp.McpTransportBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.ai.mcp.McpLoader;
import dev.enola.ai.mcp.McpServerConnectionsConfig;
import dev.enola.common.name.NamedTypedObjectProvider;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@ThreadSafe
public class MCP implements NamedTypedObjectProvider<McpToolset> {

    private final ImmutableMap<String, McpServerConnectionsConfig> configMap;
    private final ConcurrentMap<String, McpToolset> toolsetMap = new MapMaker().makeMap();

    public MCP(Iterable<McpServerConnectionsConfig> configs) {
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
        return configMap.keySet();
    }

    @Override
    public Optional<McpToolset> opt(String name) {
        var config = configMap.get(name);
        if (config == null) return Optional.empty();
        else return Optional.of(toolsetMap.computeIfAbsent(name, k -> createToolset(config, name)));
    }

    private McpToolset createToolset(McpServerConnectionsConfig config, String name) {
        var transport = McpLoader.createTransport(config, name);
        McpTransportBuilder mcpTransportBuilder = connectionParams -> transport;
        var mcpSessionManager = new McpSessionManager(null, mcpTransportBuilder);

        var toolFilter = Optional.empty();
        var objectMapper = JsonBaseModel.getMapper(); // TODO Use Enola's own instead?!
        return new McpToolset(mcpSessionManager, objectMapper, toolFilter);
    }
}
