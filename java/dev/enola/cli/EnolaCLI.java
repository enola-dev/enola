/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.cli;

import dev.enola.cli.common.*;

import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Mixin;

@Command(
        name = "enola",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        synopsisSubcommandLabel = "COMMAND",
        description = VersionProvider.DESCRIPTION,
        versionProvider = VersionProvider.class,
        subcommands = {
            HelpCommand.class,
            AutoComplete.GenerateCompletion.class,
            GenCommand.class,
            DocGenCommand.class,
            GetCommand.class,
            RosettaCommand.class,
            ServerCommand.class,
            ExecMdCommand.class,
            LoggingTestCommand.class,
            InfoCommand.class,
            ValidateCommand.class,
            CanonicalizeCommand.class,
            FetchCommand.class
        })
public class EnolaCLI extends Application {

    @Mixin LoggingMixin loggingMixin;

    static CLI cli(String... args) {
        // Add any "initialization" to class Lifecycle#start(), instead of here!
        var enola = new EnolaCLI();
        return new CLI(
                args,
                new CommandLine(enola)
                        .setUsageHelpAutoWidth(true)
                        .setCaseInsensitiveEnumValuesAllowed(true)
                        // .registerConverter(Locale.class, new LocaleConverter())
                        .setExecutionStrategy(LoggingMixin::executionStrategy)
                        .setExitCodeExceptionMapper(new KnownExitCodeExceptionMapper())
                        .setExecutionExceptionHandler(new QuietExecutionExceptionHandler(enola)));
    }

    public static void main(String[] args) {
        System.exit(cli(args).execute());
    }
}
