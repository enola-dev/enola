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
package dev.enola.ai.agent;

import com.google.common.truth.Truth;

import dev.enola.ai.adk.iri.MockLlmProvider;
import dev.enola.ai.adk.test.AgentTester;
import dev.enola.common.io.resource.EmptyResource;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.stream.Stream;

public class AgentsModelAgentifierTest {

    @Ignore // TODO
    public @Test void empty() throws IOException {
        URI defaultLLM = URI.create("mocklm:hello");
        var agentifier =
                new AgentsModelAgentifier(
                        new EmptyResource.Provider(), defaultLLM, new MockLlmProvider());
        var map = agentifier.load(Stream.of(EmptyResource.EMPTY_URI));
        Truth.assertThat(map).hasSize(1);
        var agent = map.get("default");
        var tester = new AgentTester(agent);
        tester.assertTextResponseContains("hi", "hello");
    }
}
