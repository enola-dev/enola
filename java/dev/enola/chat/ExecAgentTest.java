/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.chat;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.common.exec.vorburger.TestRunner;
import dev.enola.identity.SubjectContextKey;
import dev.enola.identity.Subjects;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class ExecAgentTest {

    @Rule public TestTLCRule rule = TestTLCRule.of(SubjectContextKey.USER, new Subjects().alice());

    Room testRoom = new Room("test");
    TestSwitchboard pbx = new TestSwitchboard();
    TestRunner testRunner = new TestRunner(0, "OUTPUT");
    Agent agent =
            new ExecAgent(
                    pbx,
                    testRunner,
                    Map.of("ls", new File("/usr/bin/ls"), "who", new File("/usr/bin/who")),
                    "$ ");

    @Test
    public void ls() {
        check("ls -l", "/usr/bin/env", "bash", "-c", "ls -l");
    }

    @Test
    public void spaceLs() {
        check(" ls -l", "/usr/bin/env", "bash", "-c", "ls -l");
    }

    @Test
    public void spaceLsSpace() {
        check(" ls -l ", "/usr/bin/env", "bash", "-c", "ls -l");
    }

    @Test
    public void spaceLsSpacesArgSpace() {
        check(" ls  -lh", "/usr/bin/env", "bash", "-c", "ls  -lh");
    }

    @Test
    public void who() {
        check("who are you");
    }

    @Test
    public void whoWithExclamationMarkPrefix() {
        check("$ who am i", "/usr/bin/env", "bash", "-c", "who am i");
    }

    @Test
    public void sosNotOnPathButOnExclusionList() {
        // The word "sos" IS in command-words.txt (because of https://github.com/sosreport/sos),
        // but if it's not installed (actually available on the PATH) then it should obviously just
        // be ignored as a command:
        check("sos help me");
    }

    private void check(String givenCommandLine, String... expectedCommandArray) {
        pbx.watch(agent);
        pbx.post(new MessageImpl.Builder().content(givenCommandLine).to(testRoom));

        if (expectedCommandArray.length > 0) {
            assertThat(testRunner.command)
                    .containsExactly((Object[]) expectedCommandArray)
                    .inOrder();

            assertThat(pbx.messages).hasSize(2);
            var echo = pbx.messages().get(1);
            assertThat(echo.content()).isEqualTo("OUTPUT");

        } else assertThat(testRunner.command).isNull();
    }
}
