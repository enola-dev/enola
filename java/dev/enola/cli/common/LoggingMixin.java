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
package dev.enola.cli.common;

import static picocli.CommandLine.ScopeType.INHERIT;
import static picocli.CommandLine.Spec.Target.MIXEE;

import dev.enola.common.Version;
import dev.enola.common.logging.JavaUtilLogging;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.logging.Level;

public class LoggingMixin {

    // https://picocli.info/#_use_case_configure_log_level_with_a_global_option

    @Spec(MIXEE)
    CommandSpec mixee;

    private static Level calcLogLevel(int level) {
        switch (level) {
            case 0:
                return Level.OFF;
            case 1:
                return Level.SEVERE; // AKA JUL ERROR
            case 2:
                return Level.WARNING;
            case 3:
                return Level.CONFIG; // incl. JUL & SLF4j INFO
            case 4:
                return Level.FINE; // incl. SLF4j DEBUG
            case 5:
                return Level.FINER; // unchanged for SLF4j
            case 6:
                return Level.FINEST; // incl. SLF4j TRACE
        }
        return Level.ALL;
    }

    public static int executionStrategy(CommandLine.ParseResult parseResult) {
        var app = (Application) parseResult.commandSpec().root().userObject();
        var level = calcLogLevel(app.loggingVerbosity);

        JavaUtilLogging.configure(level);
        app.loggingIsConfigured();
        app.log()
                .error(
                        """
                        Hi! \uD83D\uDC4B I'm https://Enola.dev {}. \
                        \uD83D\uDC7D Resistance \uD83D\uDC7E is futile. We are ONE. \
                        What's your goal, today? \
                        """,
                        Version.get());

        app.start();

        // And now back to and onwards with the default execution strategy
        return new CommandLine.RunLast().execute(parseResult);
    }

    @Option(
            names = {"--verbose", "-v"},
            scope = INHERIT,
            description = {
                "Error verbosity; specify multiple -v options to increase it; e.g. -v -v -v or"
                        + " -vvv."
            })
    public void setVerbose(boolean[] verbosity) {
        var app = (Application) mixee.root().userObject();
        app.loggingVerbosity = verbosity.length;
    }

    // NB Because slf4j-jdk14-*.jar is on the classpath,
    // we do not need to configure SLF4j, only java.util.logging;
    // see https://www.slf4j.org/manual.html#swapping.
}
