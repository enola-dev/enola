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
package dev.enola.ai.mcp.cli;

import dev.enola.ai.mcp.McpLoader;
import dev.enola.common.io.resource.ResourceProvider;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/** CLI Options related to Model Context Protocol (MCP). */
public class McpOptions {

    static final String DEFAULT_MCP_YAML = "classpath:/enola.dev/ai/mcp.yaml";

    @CommandLine.Option(
            names = {"--mcp"},
            // TODO Fix packaging to include // models
            defaultValue = DEFAULT_MCP_YAML,
            description =
                    "URL/s of MCP Configurations; see"
                            + " https://docs.enola.dev/concepts/mcp/#configuration")
    @Nullable List<URI> mcpConfigURIs;

    public static McpOptions handleDefault(@Nullable McpOptions mcpOptions) {
        if (mcpOptions == null) {
            mcpOptions = new McpOptions();
            mcpOptions.mcpConfigURIs = List.of(URI.create(McpOptions.DEFAULT_MCP_YAML));
        }
        return mcpOptions;
    }

    public void load(McpLoader loader, ResourceProvider rp) throws IOException {
        if (mcpConfigURIs == null) return;
        for (var uri : mcpConfigURIs) loader.load(rp.getNonNull(uri));
    }
}
