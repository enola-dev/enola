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

import com.google.adk.tools.GoogleSearchTool;
import com.google.adk.tools.BaseTool;
import com.google.common.collect.ImmutableMap;

import dev.enola.ai.mcp.McpLoader;
import dev.enola.ai.mcp.McpServerConnectionsConfig;
import dev.enola.common.SuccessOrError;

import java.time.InstantSource;
import java.util.Map;

public final class Tools {

    public static ToolsetProvider none() {
        return ToolsetProvider.immutableToolsets(Map.of());
    }

    public static ToolsetProvider mcp(
            Iterable<McpServerConnectionsConfig> configs, McpLoader mcpLoader) {
        return new ToolsetProviderChain(
                builtin(InstantSource.system()), new MCP(configs, mcpLoader));
    }

    public static ToolsetProvider builtin(InstantSource instantSource) {
        Map<String, BaseTool> fileSystemTools = FileSystemTools.createToolSet(new FileSystemTools());

        return ToolsetProvider.immutableTools(
                ImmutableMap.of(
                        "clock",
                        DateTimeTools.currentDateAndTimeAdkTool(new DateTimeTools(instantSource)),
                        "search_google",
                        new GoogleSearchTool(),
                        "list_directory",
                        fileSystemTools.get("listDirectory"),
                        "read_file",
                        fileSystemTools.get("readFile"),
                        "write_file",
                        fileSystemTools.get("writeFile"),
                        "edit_file",
                        fileSystemTools.get("editFile"),
                        "search_files",
                        fileSystemTools.get("searchFiles"),
                        "create_directory",
                        fileSystemTools.get("createDirectory"),
                        "grep_file",
                        fileSystemTools.get("grepFile"),
                        "execute_command",
                        fileSystemTools.get("executeCommand")
                        ));
    }

    public static Map<String, String> toMap(SuccessOrError<String> soe) {
        return soe.map(Tools::successMap, Tools::errorMap);
    }

    public static Map<String, String> successMap(String report) {
        return Map.of("status", "success", "report", report);
    }

    public static Map<String, String> errorMap(String error) {
        return Map.of("status", "error", "report", error);
    }

    private Tools() {}
}
