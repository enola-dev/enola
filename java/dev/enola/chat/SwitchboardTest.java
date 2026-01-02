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
import dev.enola.identity.SubjectContextKey;
import dev.enola.identity.Subjects;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

public class SwitchboardTest {

    @Rule public TestTLCRule rule = TestTLCRule.of(SubjectContextKey.USER, new Subjects().alice());

    @Test
    public void chit() {
        var s = new TestSwitchboard();

        var m1 = new MessageImpl.Builder();
        m1.to(new Room("Chat #1"));
        m1.content("Hello");

        s.post(m1);

        assertThat(s.messages()).hasSize(1);

        var msgs = new ArrayList<Message>();
        s.watch(msgs::add);
        assertThat(msgs).hasSize(1);
    }
}
