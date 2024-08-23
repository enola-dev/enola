/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.TestResource;
import dev.enola.common.protobuf.ProtobufMediaTypes;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EnolaCLITest {

    private static final String MODEL = "classpath:/enola.dev/enola.ttl";

    private static CLI cli;

    private static CLI cli(String... args) {
        // This was intended to make initization one time and faster, but it doesn't help.
        // TODO Profile this test and see where it spends time!
        if (cli == null) cli = dev.enola.cli.EnolaCLI.cli(args);
        else cli.setArgs(args);
        return cli;
    }

    @Test
    public void noArguments() {
        assertThat(cli()).hasExitCode(1).err().startsWith("Missing required subcommand");
    }

    @Test
    public void badArgument() {
        assertThat(cli("--bad")).hasExitCode(1).err().startsWith("Unknown option: '--bad'");
    }

    @Test
    public void help() {
        assertThat(cli("-h")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
        assertThat(cli("--help")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
    }

    @Test
    public void version() {
        assertThat(cli("-V")).hasExitCode(0).out().contains("Copyright");
        assertThat(cli("--version")).hasExitCode(0).out().contains("Copyright");
        // TODO assertThat(cli("version")).hasExitCode(0).err().contains("Copyright");
    }

    @Test
    public void docGenEmojiThing() throws IOException {
        Path dir = Files.createTempDirectory("EnolaTest");

        var exec = cli("-vvv", "docgen", "--load", MODEL, "--output", dir.toUri().toString());

        assertThat(exec).err().isEmpty();
        assertThat(exec).hasExitCode(0).out().isEmpty();
        assertThatFileContains(dir, "enola.dev/emoji.md", "Emoji");
    }

    private void assertThatFileContains(Path dir, String filePath, String contains)
            throws IOException {
        var mdPath = dir.resolve(filePath);
        var r = new FileResource(mdPath.toUri(), MediaType.PLAIN_TEXT_UTF_8);
        var md = r.charSource().read();
        assertThat(md).contains(contains);
    }

    @Test // ~same (as unit instead of integration test) also in
    // MarkdownSiteGeneratorTest#templatedGreetingN()
    public void docGenTemplatedGreetingN() throws IOException {
        Path dir = Files.createTempDirectory("EnolaTest");

        var exec =
                cli(
                        "-vvv",
                        "docgen",
                        "--variables",
                        "HTML",
                        "--load",
                        "classpath:/example.org/greetingN.ttl",
                        "--output",
                        dir.toUri().toString());

        assertThat(exec).err().isEmpty();
        assertThat(exec).hasExitCode(0).out().isEmpty();

        var expectedGreetingMD = new ClasspathResource("greeting_var-HTML.md").charSource().read();
        assertThatFileContains(dir, "example.org/greeting.md", expectedGreetingMD);

        var expectedGreetingNumberMD =
                new ClasspathResource("greet-NUMBER_var-HTML.md").charSource().read();
        assertThatFileContains(dir, "example.org/greet/_NUMBER.md", expectedGreetingNumberMD);
    }

    @Test
    public void getEntity() {
        var exec =
                cli(
                        "-v",
                        "get",
                        "--format",
                        "TextProto",
                        "--model",
                        "classpath:/cli-test-model.textproto",
                        "test.foobar/helo");
        assertThat(exec).err().isEmpty();
        assertThat(exec)
                .hasExitCode(0)
                .out()
                .startsWith(
                        """
                        id {
                          ns: "test"
                          entity: "foobar"
                          paths: "helo"
                        """);
    }

    @Test
    public void getBinaryEntity() throws IOException {
        try (var r = TestResource.create(ProtobufMediaTypes.PROTOBUF_BINARY)) {
            var exec =
                    cli(
                            "-v",
                            "get",
                            "--test-scheme",
                            "--format",
                            "BinaryPB",
                            "--output",
                            r.uri().toString(),
                            "--model",
                            "classpath:/cli-test-model.textproto",
                            "test.foobar/helo");
            assertThat(exec).err().isEmpty();
            assertThat(exec).out().isEmpty();
            assertThat(exec).hasExitCode(0);
            // Size varies because Entity contains "ts:" Timestamp
            assertThat(r.byteSource().size()).isAtLeast(100);
        }
    }

    @Test
    public void getLoadedThing() {
        var exec = cli("-vvv", "get", "--load", MODEL, "https://enola.dev/emoji");
        assertThat(exec).err().isEmpty();
        assertThat(exec).hasExitCode(0);
    }

    @Test
    public void getNonExistentThing() {
        var exec = cli("-vvv", "get", "--load", MODEL, "https://docs.enola.dev/non-existent");
        assertThat(exec).err().isEqualTo("https://docs.enola.dev/non-existent has nothing!\n");
        assertThat(exec).out().isEmpty();
        assertThat(exec).hasExitCode(0);
    }

    @Test
    public void getNonExistentTemplateIRIThing() {
        var exec = cli("-vvv", "get", "--load", MODEL, "https://docs.enola.dev/non-existent/{ID}");
        assertThat(exec).err().isEqualTo("https://docs.enola.dev/non-existent/{ID} has nothing!\n");
        assertThat(exec).out().isEmpty();
        assertThat(exec).hasExitCode(0);
    }

    @Test
    public void getList() {
        var exec = cli("-vvv", "get", "--load", MODEL, "enola:/");
        assertThat(exec).err().isEmpty();
        assertThat(exec).hasExitCode(0).out().contains("https://enola.dev/emoji");
    }

    @Test
    public void serveBothHttpAndGRPC() {
        var exec =
                cli(
                        "-v",
                        "server",
                        "--model",
                        "classpath:/cli-test-model.textproto",
                        "--httpPort=0",
                        "--grpcPort=0",
                        "--immediateExitOnlyForTest=true");
        assertThat(exec).err().isEmpty();
        var out = assertThat(exec).hasExitCode(0).out();
        out.startsWith("gRPC API server now available on port ");
        out.contains("HTTP JSON REST API + HTML UI server started; open ");
    }

    @Test
    public void serveOnlyHttp() {
        var exec =
                cli(
                        "-v",
                        "server",
                        "--model",
                        "classpath:/cli-test-model.textproto",
                        "--httpPort=0",
                        "--immediateExitOnlyForTest=true");
        assertThat(exec).err().isEmpty();
        var out = assertThat(exec).hasExitCode(0).out();
        out.startsWith("HTTP JSON REST API + HTML UI server started; open ");
        out.doesNotContain("gRPC");
    }

    @Test
    public void serveOnlyGrpc() {
        var exec =
                cli(
                        "-v",
                        "server",
                        "--model",
                        "classpath:/cli-test-model.textproto",
                        "--grpcPort=0",
                        "--immediateExitOnlyForTest=true");
        assertThat(exec).err().isEmpty();
        var out = assertThat(exec).hasExitCode(0).out();
        out.startsWith("gRPC API server now available on port ");
        out.doesNotContain("HTML");
    }

    @Test
    public void rosetta() throws IOException {
        try (var r = TestResource.create(YAML_UTF_8)) {
            var exec =
                    cli(
                            "-v",
                            "rosetta",
                            "--test-scheme",
                            "--schema",
                            "EntityKinds",
                            "--in",
                            "classpath:/cli-test-model.textproto",
                            "--out",
                            r.uri().toString());
            assertThat(exec).err().isEmpty();
            assertThat(exec).hasExitCode(0).out().isEmpty();
            assertThat(r.charSource().read()).startsWith("kinds:\n");
        }
    }

    @Test
    public void noStacktraceWithoutVerbose() {
        var exec = cli("docgen", "--model", "file:/nonexistant.yaml");
        assertThat(exec).out().isEmpty();
        assertThat(exec).hasExitCode(1).err().contains("NoSuchFileException: /nonexistant.yaml\n");
    }

    @Test
    public void stacktraceWithGlobalVerbose() {
        var exec = cli("-v", "docgen", "--model", "file:/nonexistant.yaml");
        assertThat(exec).out().isEmpty();
        assertThat(exec)
                .hasExitCode(1)
                .err()
                .startsWith("java.nio.file.NoSuchFileException: /nonexistant.yaml\n\tat ");
    }

    @Test
    public void stacktraceWithSubcommandVerbose() {
        var exec = cli("docgen", "-v", "--model", "file:/nonexistant.yaml");
        assertThat(exec).out().isEmpty();
        assertThat(exec)
                .hasExitCode(1)
                .err()
                .startsWith("java.nio.file.NoSuchFileException: /nonexistant.yaml\n\tat ");
    }
}
