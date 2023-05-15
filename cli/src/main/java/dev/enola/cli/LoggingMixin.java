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
import static picocli.CommandLine.Spec.Target.MIXEE;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingMixin {

    // https://picocli.info/#_use_case_configure_log_level_with_a_global_option

    @Spec(MIXEE)
    CommandSpec mixee;

    private static Level calcLogLevel(int level) {
        switch (level) {
            case 0:
                return Level.OFF;
            case 1:
                return Level.SEVERE;
            case 2:
                return Level.WARNING;
            case 3:
                return Level.INFO;
            case 4:
                return Level.FINE;
            case 5:
                return Level.FINER;
        }
        return Level.FINEST;
    }

    public static int executionStrategy(CommandLine.ParseResult parseResult) {
        var enola = (Enola) parseResult.commandSpec().root().userObject();
        var level = calcLogLevel(enola.verbosity.length);

        configureJUL(level);

        // And now back to and onwards with the default execution strategy
        return new CommandLine.RunLast().execute(parseResult);
    }

    private static void configureJUL(Level level) {
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n"
                // "%1$tF %1$tT %4$s %2$s %5$s%6$s%n"
                // "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n"
                );

        Logger.getLogger("ch.vorburger.exec").setLevel(level);
    }

    @Option(
            names = {"--verbose", "-v"},
            scope = INHERIT,
            description = {
                "Error verbosity; specify multiple -v options to increase it; e.g. -v -v -v or"
                        + " -vvv."
            })
    public void setVerbose(boolean[] verbosity) {
        var enola = (Enola) mixee.root().userObject();
        enola.verbosity = verbosity;
    }

    // NB Because slf4j-jdk14-*.jar is on the classpath,
    // we do not need to configure SLF4j, only java.util.logging;
    // see https://www.slf4j.org/manual.html#swapping.
}
