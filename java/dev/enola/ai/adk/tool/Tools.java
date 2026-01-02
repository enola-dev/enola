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

import dev.enola.ai.mcp.McpLoader;
import dev.enola.common.SuccessOrError;

import java.util.Map;

public final class Tools {

    public static ToolsetProvider none() {
        return ToolsetProvider.immutableToolsets(Map.of());
    }

    public static ToolsetProvider mcp(McpLoader mcpLoader) {
        return new MCP(mcpLoader);
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
