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

import static com.google.common.base.Strings.isNullOrEmpty;

import dev.enola.ai.mcp.McpLoader;
import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.object.jackson.YamlObjectReaderWriter;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.secret.auto.AutoSecretManager;

import io.modelcontextprotocol.spec.McpSchema;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "list-tools", description = "List all loaded MCP Tools")
public class ListToolsCommand implements Callable<Integer> {

    @Nullable
    @CommandLine.ArgGroup(exclusive = false)
    McpOptions mcpOptions;

    ResourceProvider rp =
            new ResourceProviders(new FileResource.Provider(), new ClasspathResource.Provider());

    YamlObjectReaderWriter objectWriter = new YamlObjectReaderWriter();

    @Override
    public Integer call() throws Exception {
        // TODO Move this somewhere else, so that it can be shared between commands
        McpLoader loader = new McpLoader(AutoSecretManager.INSTANCE());

        mcpOptions = McpOptions.handleDefault(mcpOptions);
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            mcpOptions.load(loader, rp);
        }

        Map<String, List<McpSchema.Tool>> tools = new HashMap<>();
        for (var name : loader.names()) {
            var thisTool = new ArrayList<McpSchema.Tool>();
            var optToolClient = loader.opt(name);
            if (optToolClient.isEmpty()) continue;

            var toolClient = optToolClient.get();
            var listToolsResult = toolClient.listTools();
            var nextCursor = listToolsResult.nextCursor();
            do {
                thisTool.addAll(listToolsResult.tools());
                if (!isNullOrEmpty(nextCursor)) listToolsResult = toolClient.listTools(nextCursor);
                else listToolsResult = null;
            } while (listToolsResult != null);
            tools.put(name, thisTool);
        }

        var yaml = objectWriter.write(tools, YamlMediaType.YAML_UTF_8).get();
        System.out.println(yaml);

        return 0;
    }
}
