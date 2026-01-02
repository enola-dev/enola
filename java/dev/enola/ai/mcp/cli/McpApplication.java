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

import dev.enola.cli.common.Application;
import dev.enola.cli.common.CLI;
import dev.enola.cli.common.LoggingMixin;
import dev.enola.cli.common.VersionProvider;
import dev.enola.common.io.mediatype.MarkdownMediaTypes;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;

import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "mcp",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        synopsisSubcommandLabel = "COMMAND",
        description = VersionProvider.DESCRIPTION,
        versionProvider = VersionProvider.class,
        subcommands = {
            // Generic to all CLIs
            CommandLine.HelpCommand.class,
            AutoComplete.GenerateCompletion.class,

            // Specific to this CLI
            ListToolsCommand.class,
            CallToolCommand.class
        })
public class McpApplication extends Application {

    @CommandLine.Mixin LoggingMixin loggingMixin;

    public static void main(String[] args) {
        System.exit(cli(args).execute());
    }

    static CLI cli(String... args) {
        return new CLI(args, new McpApplication());
    }

    @Override
    protected void start() {
        MediaTypeProviders.SINGLETON.set(MTP);
    }

    private static final MediaTypeProviders MTP =
            new MediaTypeProviders(
                    // NB: The order in which we list the MediaTypeProvider here matters!
                    new MarkdownMediaTypes(), new StandardMediaTypes(), new YamlMediaType());
}
