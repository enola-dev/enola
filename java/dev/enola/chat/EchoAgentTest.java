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
package dev.enola.chat;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.identity.SubjectContextKey;
import dev.enola.identity.Subjects;

import org.junit.Rule;
import org.junit.Test;

public class EchoAgentTest {

    @Rule public TestTLCRule rule = TestTLCRule.of(SubjectContextKey.USER, new Subjects().alice());

    @Test
    public void echo() {
        var pbx = new TestSwitchboard();
        var agent = new EchoAgent(pbx);
        pbx.watch(agent);

        var testRoom = new Room("test");
        pbx.post(new MessageImpl.Builder().content("@echo Hello").to(testRoom));
        assertThat(pbx.messages).hasSize(2);
        var echo = pbx.messages().get(1);
        assertThat(echo.content()).isEqualTo("Hello");
        assertThat(echo.from()).isEqualTo(agent.subject());
    }
}
