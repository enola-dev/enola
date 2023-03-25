/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import static picocli.CommandLine.ScopeType.INHERIT;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "enola",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        description = "https://enola.dev",
        versionProvider = VersionProvider.class,
        subcommands = DocGen.class)
public class CLI {

    @Option(
            names = {"--verbose", "-v"},
            scope = INHERIT,
            description = {
                "Specify multiple -v options to increase verbosity. For example, `-v -v -v` or"
                        + " `-vvv`"
            })
    boolean[] verbosity = {};

    static int exec(String[] args) {
        return new CommandLine(new CLI())
                // .registerConverter(Locale.class, new LocaleConverter())
                .setExecutionExceptionHandler(new QuietExecutionExceptionHandler())
                .execute(args);
    }

    public static void main(String[] args) {
        System.exit(exec(args));
    }

    private static class QuietExecutionExceptionHandler
            implements CommandLine.IExecutionExceptionHandler {
        @Override
        public int handleExecutionException(
                Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult)
                throws Exception {
            if (parseResult.hasMatchedOption('v')) {
                cmd.getErr().println(cmd.getColorScheme().richStackTraceString(ex));
            } else {
                Throwable e = ex;
                while (e != null) {
                    cmd.getErr().println(cmd.getColorScheme().errorText(e.getMessage()));
                    e = e.getCause();
                    if (e != null) {
                        cmd.getErr().print("caused by: ");
                    }
                }
            }
            cmd.getErr().flush();
            return cmd.getExitCodeExceptionMapper() != null
                    ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                    : cmd.getCommandSpec().exitCodeOnExecutionException();
        }
    }
}
