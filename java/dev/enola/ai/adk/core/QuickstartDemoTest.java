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
package dev.enola.ai.adk.core;

import static dev.enola.ai.iri.GoogleModelProvider.GOOGLE_AI_API_KEY_SECRET_NAME;

import com.google.adk.models.BaseLlm;

import dev.enola.ai.adk.iri.LlmProviders;
import dev.enola.ai.adk.test.AgentTester;
import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;

import org.junit.Test;

import java.io.IOException;

public class QuickstartDemoTest {

    SecretManager secretManager = new TestSecretManager();
    Provider<BaseLlm> p = new LlmProviders(secretManager);

    @Test
    public void test() throws IOException {
        if (!secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent()) return;
        var agent = QuickstartDemo.initAgent(p.get(GoogleModelProvider.FLASH));

        var tester = new AgentTester(agent);
        tester.assertTextResponseContains(
                "What's the weather in New York?", QuickstartDemo.NYC_WEATHER);

        tester.assertTextResponseContains(
                "What's the time in Zürich?", "The current time in Zürich is ");

        // TODO Test status=error vs success
        tester.assertTextResponseContains("What's the time now in Lausanne?", "Sorry, I ");
        // TODO lower temperature (?) to reduce variability?
        //   "Sorry, I don't have timezone information for Lausanne"
        //   "Sorry, I am not able to retrieve the current time in Lausanne."
    }
}
