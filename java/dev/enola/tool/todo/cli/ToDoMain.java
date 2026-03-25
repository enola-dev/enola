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
package dev.enola.tool.todo.cli;

import dev.enola.cli.common.Application;
import dev.enola.cli.common.CLI;
import dev.enola.cli.common.LocaleOption;
import dev.enola.cli.common.LoggingMixin;
import dev.enola.cli.common.VersionProvider;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.tool.todo.ToDoRepository;
import dev.enola.tool.todo.config.ToDoRepositorySupplier;

import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Mixin;

@Command(
        name = "todo",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        synopsisSubcommandLabel = "COMMAND",
        description = "ToDo List Tool",
        versionProvider = VersionProvider.class,
        subcommands = {HelpCommand.class, ToDoAddCommand.class, ToDoListCommand.class})
public class ToDoMain extends Application {

    @Mixin LoggingMixin loggingMixin;
    @Mixin LocaleOption localeOption;

    ToDoRepository repository;

    public static void main(String[] args) {
        System.exit(cli(args).execute());
    }

    private static CLI cli(String... args) {
        return new CLI(args, new ToDoMain());
    }

    @Override
    protected void start() {
        localeOption.initializeSINGLETON();

        // TODO Use a shared Configuration class, like EnolaApplication does
        if (!MediaTypeProviders.SINGLETON.isSet()) {
            MediaTypeProviders.set(new YamlMediaType());
        }

        repository = new ToDoRepositorySupplier().get();
    }
}
