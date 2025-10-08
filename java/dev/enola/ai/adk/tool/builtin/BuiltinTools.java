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
package dev.enola.ai.adk.tool.builtin;

import com.google.adk.tools.BaseTool;
import com.google.adk.tools.GoogleSearchTool;
import com.google.common.collect.ImmutableMap;

import dev.enola.ai.adk.tool.Tools;
import dev.enola.ai.adk.tool.ToolsetProvider;
import dev.enola.ai.adk.tool.ToolsetProviderChain;
import dev.enola.ai.mcp.McpLoader;
import dev.enola.tool.todo.ai.tool.adk.ToDoTool;
import dev.enola.tool.todo.config.ToDoRepositorySupplier;

import java.time.InstantSource;

public final class BuiltinTools {

    public static ToolsetProvider builtinAndMcp(McpLoader mcpLoader) {
        return new ToolsetProviderChain(builtin(), Tools.mcp(mcpLoader));
    }

    public static ToolsetProvider builtin() {
        return builtin(InstantSource.system());
    }

    public static ToolsetProvider builtin(InstantSource instantSource) {
        var tools = ImmutableMap.<String, BaseTool>builder();

        var dateTimeTools = new DateTimeTools(instantSource);
        tools.put("clock", DateTimeTools.currentDateAndTimeAdkTool(dateTimeTools));

        tools.put("search_google", new GoogleSearchTool());
        tools.putAll(new FileSystemTools().createToolSet());
        tools.put("exec", new ExecTool().createTool());

        var toDoRepository = new ToDoRepositorySupplier().get();
        tools.putAll(new ToDoTool(toDoRepository).createToolSet());

        return ToolsetProvider.immutableTools(tools.build());
    }

    private BuiltinTools() {}
}
