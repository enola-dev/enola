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

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class CLITest {

    // TODO assert output
    // with https://truth.dev/extension.html for
    // https://picocli.info/#_black_box_and_white_box_testing

    // TODO testVerbose()

    @Test
    public void testNoArguments() {
        assertThat(CLI.exec(new String[] {})).isEqualTo(2);
        // TODO withOutput().startsWith("Missing required subcommand")
    }

    @Test
    public void testBadArgument() {
        assertThat(CLI.exec(new String[] {"-bad"})).isEqualTo(2);
        // TODO withOutput().startsWith("Unknown option: '-b'");
    }

    @Test
    public void testHelp() {
        assertThat(CLI.exec(new String[] {"-h"})).isEqualTo(0);
        assertThat(CLI.exec(new String[] {"--help"})).isEqualTo(0);
        // TODO withOutput().startsWith("Usage: enola [-hVv] [COMMAND]")
    }

    @Test
    public void testVersion() {
        assertThat(CLI.exec(new String[] {"-V"})).isEqualTo(0);
        assertThat(CLI.exec(new String[] {"--version"})).isEqualTo(0);
    }

    @Test
    public void testDocGen() {
        assertThat(CLI.exec(new String[] {"docgen"})).isEqualTo(17);
    }
}
