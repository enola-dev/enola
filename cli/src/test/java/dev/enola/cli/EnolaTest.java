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

import static dev.enola.cli.CommandLineSubject.assertThat;
import static dev.enola.cli.Enola.cli;

import com.google.common.net.MediaType;
import com.google.common.truth.Truth;

import dev.enola.common.io.resource.TestResource;
import dev.enola.core.meta.docgen.MarkdownDocGenerator;

import org.junit.Test;

import java.io.IOException;

public class EnolaTest {

    // TODO testVerbose()

    @Test
    public void testNoArguments() {
        assertThat(cli()).hasExitCode(2).err().startsWith("Missing required subcommand");
    }

    @Test
    public void testBadArgument() {
        assertThat(cli("--bad")).hasExitCode(2).err().startsWith("Unknown option: '--bad'");
    }

    @Test
    public void testHelp() {
        assertThat(cli("-h")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
        assertThat(cli("--help")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
    }

    @Test
    public void testVersion() {
        assertThat(cli("-V")).hasExitCode(0).out().contains("Copyright");
        assertThat(cli("--version")).hasExitCode(0).out().contains("Copyright");
        // TODO assertThat(cli("version")).hasExitCode(0).err().contains("Copyright");
    }

    @Test
    public void testDocGen() throws IOException {
        try (var r = TestResource.create(MediaType.PLAIN_TEXT_UTF_8)) {
            var exec =
                    cli(
                            "-v",
                            "docgen",
                            "--model",
                            "classpath:cli-test-model.textproto",
                            "--output",
                            r.uri().toString());
            assertThat(exec).err().isEmpty();
            assertThat(exec).hasExitCode(0).out().isEmpty();
            Truth.assertThat(r.charSource().read()).endsWith(MarkdownDocGenerator.FOOTER);
        }
    }

    @Test
    public void testListKind() {
        var exec = cli("-v", "list-kinds", "--model", "classpath:cli-test-model.textproto");
        assertThat(exec).err().isEmpty();
        assertThat(exec).hasExitCode(0).out().isEqualTo("test.foobar/name\n");
    }

    @Test
    public void testGet() {
        var exec =
                cli(
                        "-v",
                        "get",
                        "--model",
                        "classpath:cli-test-model.textproto",
                        "test.foobar/helo");
        assertThat(exec).err().isEmpty();
        assertThat(exec)
                .hasExitCode(0)
                .out()
                .startsWith(
                        "id {\n"
                                + "  ns: \"test\"\n"
                                + "  entity: \"foobar\"\n"
                                + "  paths: \"helo\"\n");
    }
}
