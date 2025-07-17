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
package dev.enola.ai.dotprompt.adk;

import com.google.adk.models.BaseLlm;

import dev.enola.ai.adk.iri.LlmProviders;
import dev.enola.ai.adk.test.AgentTester;
import dev.enola.ai.iri.Provider;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class DotPrompt2LlmAgentTest {

    URI defaultLLM = URI.create("mocklm:hello");
    SecretManager secretManager = new TestSecretManager();
    Provider<BaseLlm> llmProvider = new LlmProviders(secretManager);

    ResourceProviders rp =
            new ResourceProviders(new EmptyResource.Provider(), new ClasspathResource.Provider());
    DotPrompt2LlmAgent loader = new DotPrompt2LlmAgent(rp, defaultLLM, llmProvider);

    @Test
    public void empty() throws IOException {
        var agent = loader.load(EmptyResource.EMPTY_TEXT_URI);
        var tester = new AgentTester(agent);

        tester.assertTextResponseContains("hi", "hello");
    }

    @Test
    public void person() throws IOException {
        var agent = loader.load(URI.create("classpath:/prompts/person.prompt.md"));
        var tester = new AgentTester(agent);

        // TODO Replace with TestPrompt etc. stuff to validate test/text/expected in .prompt.md
        tester.assertJsonResponseEquals(
                "John Doe is a 35-year-old software engineer living in Zürich.",
                """
                { "name": "John Doe", "age": 35, "occupation": "software engineer" }\
                """);
    }
}
