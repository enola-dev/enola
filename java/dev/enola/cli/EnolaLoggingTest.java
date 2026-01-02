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
import static dev.enola.cli.EnolaApplication.cli;
import static dev.enola.common.context.testlib.SingletonRule.onlyReset;

import dev.enola.common.context.testlib.SingletonRule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.logging.Logger;

public class EnolaLoggingTest {

    public @Rule SingletonRule rule = onlyReset(Configuration.singletons());

    // NB: As per JavaDoc of SystemOutErrCapture, this doesn't work that well with JUL,
    // because it retains the System.err in a static which we cannot (easily) clear.
    // The following is thus set up like it is to make it clear that this captures
    // the output of all tests, not just the testLoggingVerbosity.  It "works"
    // because this is the only test (in this module).
    // TODO Try if LogManager.getLogManager().reset(); could fix ^^^ this?

    private static final SystemOutErrCapture capture = new SystemOutErrCapture();

    @AfterClass
    public static void tearDown() throws Exception {
        capture.close();
    }

    @Before
    public void clear() {
        // Clears previous test's output,
        // and "JUnit4 Test Runner ..E....E......." at the start.
        capture.clear();
    }

    @Test
    public void testLoggingVerbosity0() throws Exception {
        assertThat(cli("test-logging")).hasExitCode(0);
        assertThat(capture.getSystemOut()).isEmpty();
        assertThat(capture.getSystemErr()).isEmpty();
        capture.clear();

        var rootLogger = Logger.getLogger("");
        assertThat(rootLogger.getHandlers()).hasLength(1);
    }

    @Test
    public void testLoggingVerbosity1() throws Exception {
        assertThat(cli("-v", "test-logging")).hasExitCode(0);
        assertThat(capture.getSystemErr()).contains("SLF ERROR");
        assertThat(capture.getSystemErr()).contains("JUL SEVERE");
        assertThat(capture.getSystemErr()).doesNotContain("WARN");
        capture.clear();
    }

    @Test
    public void testLoggingVerbosity2() throws Exception {
        assertThat(cli("-vv", "test-logging")).hasExitCode(0);
        assertThat(capture.getSystemErr()).contains("SLF ERROR");
        assertThat(capture.getSystemErr()).contains("JUL SEVERE");
        assertThat(capture.getSystemErr()).contains("SLF WARN");
        assertThat(capture.getSystemErr()).contains("JUL WARNING");
        assertThat(capture.getSystemErr()).doesNotContain("INFO");
    }

    @Test
    public void testLoggingVerbosity3() throws Exception {
        assertThat(cli("-vvv", "test-logging")).hasExitCode(0);
        assertThat(capture.getSystemErr()).contains("SLF ERROR");
        assertThat(capture.getSystemErr()).contains("JUL SEVERE");
        assertThat(capture.getSystemErr()).contains("SLF WARN");
        assertThat(capture.getSystemErr()).contains("JUL WARNING");
        assertThat(capture.getSystemErr()).contains("SLF INFO");
        assertThat(capture.getSystemErr()).contains("JUL INFO");
        assertThat(capture.getSystemErr()).contains("JUL CONFIG");
        assertThat(capture.getSystemErr()).doesNotContain("DEBUG");
        assertThat(capture.getSystemErr()).doesNotContain("FINE");
    }

    @Test
    public void testLoggingVerbosity4() throws Exception {
        assertThat(cli("-vvvv", "test-logging")).hasExitCode(0);
        assertThat(capture.getSystemErr()).contains("SLF ERROR");
        assertThat(capture.getSystemErr()).contains("JUL SEVERE");
        assertThat(capture.getSystemErr()).contains("SLF WARN");
        assertThat(capture.getSystemErr()).contains("JUL WARNING");
        assertThat(capture.getSystemErr()).contains("SLF INFO");
        assertThat(capture.getSystemErr()).contains("JUL INFO");
        assertThat(capture.getSystemErr()).contains("JUL CONFIG");
        assertThat(capture.getSystemErr()).contains("SLF DEBUG");
        assertThat(capture.getSystemErr()).contains("JUL FINE");
        assertThat(capture.getSystemErr()).doesNotContain("SLF FINE");
        assertThat(capture.getSystemErr()).doesNotContain("TRACE");
        assertThat(capture.getSystemErr()).doesNotContain("FINER");
    }

    @Test
    public void testLoggingVerbosity5() throws Exception {
        assertThat(cli("-vvvvv", "test-logging")).hasExitCode(0);
        assertThat(capture.getSystemErr()).contains("SLF ERROR");
        assertThat(capture.getSystemErr()).contains("JUL SEVERE");
        assertThat(capture.getSystemErr()).contains("SLF WARN");
        assertThat(capture.getSystemErr()).contains("JUL WARNING");
        assertThat(capture.getSystemErr()).contains("SLF INFO");
        assertThat(capture.getSystemErr()).contains("JUL INFO");
        assertThat(capture.getSystemErr()).contains("JUL FINE");
        assertThat(capture.getSystemErr()).contains("JUL CONFIG");
        assertThat(capture.getSystemErr()).contains("SLF DEBUG");
        assertThat(capture.getSystemErr()).contains("JUL FINER");
        assertThat(capture.getSystemErr()).doesNotContain("SLF TRACE");
        assertThat(capture.getSystemErr()).doesNotContain("JUL FINEST");
    }

    @Test
    public void testLoggingVerbosity6() throws Exception {
        assertThat(cli("-vvvvvv", "test-logging")).hasExitCode(0);
        assertThat(capture.getSystemErr()).contains("SLF ERROR");
        assertThat(capture.getSystemErr()).contains("JUL SEVERE");
        assertThat(capture.getSystemErr()).contains("SLF WARN");
        assertThat(capture.getSystemErr()).contains("JUL WARNING");
        assertThat(capture.getSystemErr()).contains("SLF INFO");
        assertThat(capture.getSystemErr()).contains("JUL INFO");
        assertThat(capture.getSystemErr()).contains("JUL FINE");
        assertThat(capture.getSystemErr()).contains("JUL CONFIG");
        assertThat(capture.getSystemErr()).contains("SLF DEBUG");
        assertThat(capture.getSystemErr()).contains("JUL FINER");
        assertThat(capture.getSystemErr()).contains("SLF TRACE");
        assertThat(capture.getSystemErr()).contains("JUL FINEST");
    }
}
