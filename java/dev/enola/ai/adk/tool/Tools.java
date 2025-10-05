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

import com.google.adk.tools.BaseTool;
import com.google.adk.tools.GoogleSearchTool;

import dev.enola.ai.mcp.McpLoader;
import dev.enola.common.SuccessOrError;

import java.time.InstantSource;
import java.util.HashMap;
import java.util.Map;

public final class Tools {

    public static ToolsetProvider none() {
        return ToolsetProvider.immutableToolsets(Map.of());
    }

    public static ToolsetProvider mcp(McpLoader mcpLoader) {
        return new ToolsetProviderChain(builtin(InstantSource.system()), new MCP(mcpLoader));
    }

    public static ToolsetProvider builtin(InstantSource instantSource) {
        var tools = new HashMap<String, BaseTool>();

        tools.put(
                "clock", DateTimeTools.currentDateAndTimeAdkTool(new DateTimeTools(instantSource)));
        tools.put("search_google", new GoogleSearchTool());
        tools.putAll(new FileSystemTools().createToolSet());
        tools.put("exec", new ExecTool().createTool());

        return ToolsetProvider.immutableTools(tools);
    }

    public static <T> Map<String, ?> toMap(SuccessOrError<T> soe) {
        return soe.map(Tools::successMap, Tools::errorMap);
    }

    public static <T> Map<String, ?> successMap(T report) {
        return Map.of("status", "success", "report", report);
    }

    public static <T> Map<String, ?> errorMap(T error) {
        return Map.of("status", "error", "report", error);
    }

    private Tools() {}
}
