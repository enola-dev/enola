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

import static dev.enola.cli.CommandLineSubject.assertThat;
import static dev.enola.cli.Enola.cli;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.TestResource;
import dev.enola.core.meta.docgen.MarkdownDocGenerator;

import org.junit.Test;

public class EnolaTest {

    // TODO testVerboseLogging - add logs

    @Test
    public void noArguments() throws Exception {
        assertThat(cli()).hasExitCode(1).err().startsWith("Missing required subcommand");
    }

    @Test
    public void badArgument() throws Exception {
        assertThat(cli("--bad")).hasExitCode(1).err().startsWith("Unknown option: '--bad'");
    }

    @Test
    public void help() throws Exception {
        assertThat(cli("-h")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
        assertThat(cli("--help")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
    }

    @Test
    public void version() throws Exception {
        assertThat(cli("-V")).hasExitCode(0).out().contains("Copyright");
        assertThat(cli("--version")).hasExitCode(0).out().contains("Copyright");
        // TODO assertThat(cli("version")).hasExitCode(0).err().contains("Copyright");
    }

    @Test
    public void docGen() throws Exception {
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
            assertThat(r.charSource().read()).endsWith(MarkdownDocGenerator.FOOTER);
        }
    }

    @Test
    public void listKind() throws Exception {
        var exec = cli("-v", "list-kinds", "--model", "classpath:cli-test-model.textproto");
        assertThat(exec).err().isEmpty();
        assertThat(exec)
                .hasExitCode(0)
                .out()
                .isEqualTo("enola.entity_kind/name\ntest.foobar/name\n");
    }

    @Test
    public void get() throws Exception {
        var exec =
                cli(
                        "-v",
                        "get",
                        "--format",
                        "TextProto",
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

    @Test
    public void serve() throws Exception {
        var exec =
                cli(
                        "-v",
                        "server",
                        "--model",
                        "classpath:cli-test-model.textproto",
                        "--httpPort=0",
                        "--grpcPort=0");
        exec.executeAsync();
        exec.close();
        assertThat(exec).err().isEmpty();
        assertThat(exec).hasExitCode(0).out().startsWith("gRPC API server now available on port ");
    }

    @Test
    public void rosetta() throws Exception {
        try (var r = TestResource.create(YAML_UTF_8)) {
            var exec =
                    cli(
                            "-v",
                            "rosetta",
                            "--schema",
                            "EntityKinds",
                            "--in",
                            "classpath:cli-test-model.textproto",
                            "--out",
                            r.uri().toString());
            assertThat(exec).err().isEmpty();
            assertThat(exec).hasExitCode(0).out().isEmpty();
            assertThat(r.charSource().read()).startsWith("kinds:\n");
        }
    }

    @Test
    public void noStacktraceWithoutVerbose() throws Exception {
        var exec = cli("docgen", "--model", "file:nonexistant.yaml");
        assertThat(exec).out().isEmpty();
        assertThat(exec).hasExitCode(1).err().isEqualTo("NoSuchFileException: nonexistant.yaml\n");
    }

    @Test
    public void stacktraceWithGlobalVerbose() throws Exception {
        var exec = cli("-v", "docgen", "--model", "file:nonexistant.yaml");
        assertThat(exec).out().isEmpty();
        assertThat(exec)
                .hasExitCode(1)
                .err()
                .startsWith("java.nio.file.NoSuchFileException: nonexistant.yaml\n\tat ");
    }

    @Test
    public void stacktraceWithSubcommandVerbose() throws Exception {
        var exec = cli("docgen", "-v", "--model", "file:nonexistant.yaml");
        assertThat(exec).out().isEmpty();
        assertThat(exec)
                .hasExitCode(1)
                .err()
                .startsWith("java.nio.file.NoSuchFileException: nonexistant.yaml\n\tat ");
    }
}
