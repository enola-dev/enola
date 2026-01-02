/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import dev.enola.cli.common.CLI;

public final class CommandLineSubject extends Subject {

    private final CLI actual;

    public static CommandLineSubject assertThat(CLI actual) {
        var subject = commandLines();
        actual.setOutAndErrStrings();
        actual.execute();
        return assertAbout(subject).that(actual);
    }

    public static Factory<CommandLineSubject, CLI> commandLines() {
        return CommandLineSubject::new;
    }

    private CommandLineSubject(FailureMetadata metadata, CLI actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public CommandLineSubject hasExitCode(int exitCode) {
        check("exitCode()").that(actual.exitCode()).isEqualTo(exitCode);
        return this;
    }

    public StringSubject out() {
        return check("out()").that(actual.getOutString());
    }

    public StringSubject err() {
        return check("err()").that(actual.getErrString());
    }
}
