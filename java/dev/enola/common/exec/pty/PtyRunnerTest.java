/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.exec.pty;

import static com.google.common.truth.Truth.*;

import static java.nio.charset.StandardCharsets.US_ASCII;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.github.valfirst.slf4jtest.TestLoggerFactoryResetRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.event.Level;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

public class PtyRunnerTest {

    // TODO Eventually merge this into vorburger:exec

    // TODO Run these tests against all Runner impls like vorburger:exec

    @Rule public TestRule resetLoggingEvents = new TestLoggerFactoryResetRule();
    TestLogger ptyRunnerLogger = TestLoggerFactory.getTestLogger(PtyRunner.class);
    TestLogger streamPumperLogger = TestLoggerFactory.getTestLogger(StreamPumper.class);

    Map<String, String> env = Collections.emptyMap();
    InputStream noInput = InputStream.nullInputStream();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Test
    public void echo() throws IOException {
        TestLoggerFactory.getInstance().setPrintLevel(Level.TRACE);
        try (var r =
                new PtyRunner(
                        false,
                        new String[] {"/usr/bin/echo", "hello,", "world"},
                        env,
                        noInput,
                        out,
                        err)) {
            assertThat(r.waitFor(Duration.ofSeconds(7))).isEqualTo(0);
        }
        assertThat(ptyRunnerLogger.getAllLoggingEvents()).isEmpty();
        assertThat(streamPumperLogger.getAllLoggingEvents()).isEmpty();
        assertThat(err.toString(US_ASCII)).isEmpty();
        assertThat(out.toString(US_ASCII)).isEqualTo("hello, world\r\n");
    }

    @Test
    public void cat() throws IOException {
        TestLoggerFactory.getInstance().setPrintLevel(Level.TRACE);
        var in = new ByteArrayInputStream("hello, world\r\n".getBytes(US_ASCII));
        try (var r = new PtyRunner(false, new String[] {"/usr/bin/cat"}, env, in, out, err)) {
            assertThat(r.waitFor(Duration.ofSeconds(7))).isEqualTo(0);
        }
        assertThat(ptyRunnerLogger.getAllLoggingEvents()).isEmpty();
        assertThat(streamPumperLogger.getAllLoggingEvents()).isEmpty();
        assertThat(err.toString(US_ASCII)).isEmpty();
        assertThat(in.available()).isEqualTo(0);
        assertThat(out.toString(US_ASCII)).isEqualTo("hello, world\r\n\r\n");
    }

    @Test
    public void head() throws IOException {
        TestLoggerFactory.getInstance().setPrintLevel(Level.TRACE);
        var in = new ByteArrayInputStream("line 1\nline 2\nline 3\n".getBytes(US_ASCII));
        // NB: The "head -n 1" command reads only the first line, ignored lines 2 & 3, and exits.
        try (var r =
                new PtyRunner(
                        false, new String[] {"/usr/bin/head", "-n", "1"}, env, in, out, err)) {
            assertThat(r.waitFor(Duration.ofSeconds(7))).isEqualTo(0);
        }
        assertThat(ptyRunnerLogger.getAllLoggingEvents()).isEmpty();
        assertThat(streamPumperLogger.getAllLoggingEvents()).isEmpty();
        assertThat(err.toString(US_ASCII)).isEmpty();
        assertThat(out.toString(US_ASCII)).isEqualTo("line 1\r\n");
        // The input stream was not fully consumed, which is correct (in this case)
        // TODO FIXME assertThat(in.available()).isGreaterThan(0);
        assertThat(in.available()).isEqualTo(0);
    }

    @Test(expected = IOException.class)
    public void failNotFound() throws IOException {
        try (var r =
                new PtyRunner(false, new String[] {"does-not-exist"}, env, noInput, out, err)) {}
    }
}
