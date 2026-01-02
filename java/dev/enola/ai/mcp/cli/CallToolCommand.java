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
import dev.enola.common.concurrent.Threads;
import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.secret.auto.AutoSecretManager;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "call-tool", description = "Calls a tool on an MCP Server")
public class CallToolCommand implements Callable<Integer> {

    @Nullable
    @CommandLine.ArgGroup(exclusive = false)
    McpOptions mcpOptions;

    // For testing (only)
    @CommandLine.Option(
            names = {"--wait"},
            hidden = true)
    boolean wait;

    @CommandLine.Parameters(index = "0", paramLabel = "server", description = "Server")
    String server;

    @CommandLine.Parameters(index = "1", paramLabel = "tool", description = "Tool")
    String tool;

    @CommandLine.Parameters(index = "2", paramLabel = "args", description = "Arguments (as JSON)")
    String argumentsAsJson;

    ResourceProvider rp =
            new ResourceProviders(new FileResource.Provider(), new ClasspathResource.Provider());

    @Override
    public Integer call() throws Exception {
        // TODO Move this somewhere else, so that it can be shared between commands
        McpLoader loader = new McpLoader(AutoSecretManager.INSTANCE());

        mcpOptions = McpOptions.handleDefault(mcpOptions);
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            mcpOptions.load(loader, rp);
        }
        var callToolResult = loader.call("CLI", server, tool, argumentsAsJson);
        for (var content : callToolResult.content()) {
            System.out.println(content);
        }

        if (wait) Threads.sleep(Duration.ofDays(1));

        var error = callToolResult.isError();
        return Boolean.TRUE.equals(error) ? 1 : 0;
    }
}
