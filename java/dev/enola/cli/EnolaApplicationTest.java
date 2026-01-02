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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.cli.CommandLineSubject.assertThat;
import static dev.enola.common.context.testlib.SingletonRule.onlyReset;
import static dev.enola.thing.io.ThingMediaTypes.THING_YAML_UTF_8;

import com.google.common.net.MediaType;

import dev.enola.cli.common.CLI;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.TestResource;
import dev.enola.common.protobuf.ProtobufMediaTypes;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// See also //test-cli.bash
public class EnolaApplicationTest {

    private static final String MODEL = "classpath:/enola.dev/enola.ttl";
    private static CLI cli;

    public @Rule SingletonRule rule = onlyReset(Configuration.singletons());

    private static CLI cli(String... args) {
        // This was intended to make initialization one time and faster, but it doesn't help.
        // TODO Profile this test and see where it spends time!
        if (cli == null) cli = EnolaApplication.cli(args);
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
        rule.doNotReset();
    }

    @Test
    public void help() {
        assertThat(cli("--help")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
    }

    @Test
    public void h() {
        assertThat(cli("-h")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
    }

    @Test
    public void version() {
        assertThat(cli("--version")).hasExitCode(0).out().contains("Copyright");
        // TODO assertThat(cli("version")).hasExitCode(0).err().contains("Copyright");
    }

    @Test
    public void v() {
        assertThat(cli("-V")).hasExitCode(0).out().contains("Copyright");
    }

    @Test
    public void docGenEmojiThing() throws IOException {
        Path dir = Files.createTempDirectory("EnolaTest");

        var exec = cli("-vvv", "docgen", "--load", MODEL, "--output", dir.toUri().toString());

        var run = assertThat(exec);
        run.err().isEmpty();
        run.hasExitCode(0).out().isEmpty();
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

        var subject = assertThat(exec);
        subject.err().isEmpty();
        subject.hasExitCode(0).out().isEmpty();

        var expectedGreetingMD = new ClasspathResource("greeting_var-HTML.md").charSource().read();
        assertThatFileContains(dir, "example.org/greeting.md", expectedGreetingMD);

        var expectedGreetingNumberMD =
                new ClasspathResource("greet-NUMBER_var-HTML.md").charSource().read();
        assertThatFileContains(dir, "example.org/greet/_NUMBER.md", expectedGreetingNumberMD);
    }

    @Test
    public void getThing() {
        var exec =
                cli(
                        "-v",
                        "get",
                        "--format",
                        "TextProto",
                        "--load",
                        "classpath:/picasso.ttl",
                        "http://example.enola.dev/Picasso");
        var run = assertThat(exec);
        run.err().isEmpty();
        run.hasExitCode(0)
                .out()
                .startsWith(
                        """
                        iri: "http://example.enola.dev/Picasso"
                        properties {
                          key: "http://example.enola.dev/homeAddress"
                          value {
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
                            "--load",
                            "classpath:/picasso.ttl",
                            "http://example.enola.dev/Picasso");
            var run = assertThat(exec);
            run.err().isEmpty();
            run.out().isEmpty();
            run.hasExitCode(0);
            // Size varies because Entity contains "ts:" Timestamp
            assertThat(r.byteSource().size()).isAtLeast(100);
        }
    }

    @Test
    public void getLoadedThing() {
        var exec = cli("-vvv", "get", "--load", MODEL, "https://enola.dev/emoji");
        var subject = assertThat(exec);
        subject.err().isEmpty();
        subject.out().contains("enola:emoji \"\uD83D\uDE03\";"); // 😃
        subject.hasExitCode(0);
    }

    @Test
    public void getLoadedClassAssertPropertiesDomainInverse() {
        // This ensures that the RDFSPropertyTrigger did its job
        var exec = cli("-vvv", "get", "--load", MODEL, "https://enola.dev/Event");
        var subject = assertThat(exec);
        subject.err().isEmpty();
        subject.out().contains("enola:properties enola:timestamp");
        subject.hasExitCode(0);
    }

    @Test
    public void getNonExistentThing() {
        var exec = cli("-vvv", "get", "--load", MODEL, "https://docs.enola.dev/non-existent");
        var run = assertThat(exec);
        run.err().isEqualTo("https://docs.enola.dev/non-existent has nothing!\n");
        run.out().isEmpty();
        run.hasExitCode(0);
    }

    @Test
    public void getNonExistentTemplateIRIThing() {
        var exec = cli("-vvv", "get", "--load", MODEL, "https://docs.enola.dev/non-existent/{ID}");
        var run = assertThat(exec);
        run.err().isEqualTo("https://docs.enola.dev/non-existent/{ID} has nothing!\n");
        run.out().isEmpty();
        run.hasExitCode(0);
    }

    @Test
    public void getList() {
        var exec = cli("-vvv", "get", "--load", MODEL, "enola:/");
        var run = assertThat(exec);
        run.err().isEmpty();
        run.hasExitCode(0).out().contains("enola:emoji");
    }

    @Test
    public void serveBothHttpAndGRPC() {
        var exec =
                cli(
                        "-v",
                        "server",
                        "--load",
                        "classpath:/picasso.ttl",
                        "--httpPort=0",
                        "--grpcPort=0",
                        "--immediateExitOnlyForTest=true");
        var subject = assertThat(exec);
        subject.err().isEmpty();
        var out = subject.hasExitCode(0).out();
        out.startsWith("gRPC API server now available on port ");
        out.contains("HTTP JSON REST API + HTML UI server started; open ");
    }

    @Test
    public void serveOnlyHttp() {
        var exec =
                cli(
                        "-v",
                        "server",
                        "--load",
                        "classpath:/picasso.ttl",
                        "--httpPort=0",
                        "--immediateExitOnlyForTest=true");
        var run = assertThat(exec);
        run.err().isEmpty();
        var out = run.hasExitCode(0).out();
        out.startsWith("HTTP JSON REST API + HTML UI server started; open ");
        out.doesNotContain("gRPC");
    }

    @Test
    public void serveOnlyGrpc() {
        var exec =
                cli(
                        "-v",
                        "server",
                        "--load",
                        "classpath:/picasso.ttl",
                        "--grpcPort=0",
                        "--immediateExitOnlyForTest=true");
        var run = assertThat(exec);
        run.err().isEmpty();
        var out = run.hasExitCode(0).out();
        out.startsWith("gRPC API server now available on port ");
        out.doesNotContain("HTML");
    }

    @Test
    public void serveOnlyChat() {
        var exec = cli("-v", "server", "--chatPort=0", "--immediateExitOnlyForTest=true");
        var run = assertThat(exec);
        run.err().isEmpty();
        var out = run.hasExitCode(0).out();
        out.startsWith("HTTP Chat UI server started");
        out.doesNotContain("HTML");
    }

    @Test // NB: RosettaTest has more
    public void rosetta() throws IOException {
        try (var r = TestResource.create(THING_YAML_UTF_8)) {
            var exec =
                    cli(
                            "-v",
                            "rosetta",
                            "--test-scheme",
                            "--in",
                            "classpath:/picasso.ttl",
                            "--out",
                            r.uri().toString());
            var run = assertThat(exec);
            run.err().isEmpty();
            run.hasExitCode(0).out().isEmpty();
            assertThat(r.charSource().read())
                    .startsWith("things:\n- iri: http://example.enola.dev/Dalí");
        }
    }

    @Test
    public void noStacktraceWithoutVerbose() {
        var exec = cli("docgen", "--load", "file:/nonexistant.yaml");
        var run = assertThat(exec);
        run.out().isEmpty();
        run.hasExitCode(1).err().contains("NoSuchFileException: /nonexistant.yaml\n");
    }

    @Test
    public void stacktraceWithGlobalVerbose() {
        var exec = cli("-v", "docgen", "--load", "file:/nonexistant.yaml");
        var run = assertThat(exec);
        run.out().isEmpty();
        run.hasExitCode(1)
                .err()
                .contains("Caused by: java.nio.file.NoSuchFileException: /nonexistant.yaml");
    }

    @Test
    public void stacktraceWithSubcommandVerbose() {
        var exec = cli("docgen", "-v", "--load", "file:/nonexistant.yaml");
        var run = assertThat(exec);
        run.out().isEmpty();
        run.hasExitCode(1)
                .err()
                .contains("Caused by: java.nio.file.NoSuchFileException: /nonexistant.yaml");
    }

    @Test
    @Ignore // JUST for debugging
    public void modelsDocGen() {
        var exec =
                assertThat(
                        cli(
                                "-vvv",
                                "docgen",
                                "--load",
                                "/home/vorburger/git/github.com/enola-dev/enola/docs/models/**.{ttl}",
                                "--output",
                                "/tmp/EnolaCLITest/modelsDocGen/"));
        exec.err().isEmpty();
        exec.hasExitCode(0);
    }

    @Test
    public void getLoadTikaMediaTypes() {
        var exec =
                assertThat(cli("-vvv", "get", "--load", "enola:TikaMediaTypes", "enola:/inline"));
        exec.err().isEmpty();
        exec.hasExitCode(0);
    }

    @Test
    @Ignore // TODO This causes serveOnlyChat(), which also uses ADK, to fail.
    public void aiEcho() {
        var exec = assertThat(cli("-vvv", "ai", "--llm=echo:/", "--in=hello, world"));
        exec.err().isEmpty();
        exec.out().isEqualTo("hello, world\n");
        exec.hasExitCode(0);
    }

    @Test
    @Ignore // TODO Make CLI tests isolated so that this test does not break because -vvv elsewhere
    public void exception() {
        var exec = assertThat(cli("test-exception"));
        exec.err()
                .isEqualTo(
                        "Internal Problem occurred, add -vvv flags for technical details:"
                                + " RuntimeException: Test Exception\n"
                                + "caused by: IOException: Test I/O failure\n");
        exec.out().isEmpty();
        exec.hasExitCode(1);
    }

    @Test
    @Ignore // TODO Make CLI stateless so that enabling this does not break the exception() test
    public void exceptionWithLogging() {
        var exec = assertThat(cli("-v", "test-exception"));
        exec.err().contains("java.lang.RuntimeException: Test Exception");
        exec.err().contains("at dev.enola.cli.ExceptionTestCommand.run(ExceptionTestCommand.java:");
        exec.err().contains("Caused by: java.io.IOException: Test I/O failure");
        exec.out().contains("\uD83D\uDC7D Resistance \uD83D\uDC7E is futile. We are ONE.");
        exec.out().isEmpty();
        exec.hasExitCode(1);
    }
}
