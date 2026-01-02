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
package dev.enola.ai.adk.core;

import com.google.adk.agents.LlmAgent;

import dev.enola.ai.adk.test.AgentTester;
import dev.enola.ai.adk.test.EchoModel;

import org.junit.Ignore;
import org.junit.Test;

public class UserContentReplacingAgentTest {

    @Test
    @Ignore // TODO https://github.com/google/adk-java/issues/288
    public void replace() {
        var model = new EchoModel();
        var agent1 = new LlmAgent.Builder().name("LLM").model(model).build();
        var agent2 = new UserContentReplacingAgent("test", "", text -> text + "baz", agent1);
        var tester = new AgentTester(agent2);

        tester.assertTextResponseEquals("foo", "foobar");
    }
}
