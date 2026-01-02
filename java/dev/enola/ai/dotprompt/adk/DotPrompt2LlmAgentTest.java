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
package dev.enola.ai.dotprompt.adk;

import static dev.enola.ai.iri.GoogleModelProvider.GOOGLE_AI_API_KEY_SECRET_NAME;

import com.google.adk.models.BaseLlm;

import dev.enola.ai.adk.iri.LlmProviders;
import dev.enola.ai.adk.test.AgentTester;
import dev.enola.ai.dotprompt.DotPromptLoader;
import dev.enola.ai.iri.EchoModelProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class DotPrompt2LlmAgentTest {

    // TODO Use TestsLlmProvider ? Or why not just directly MockChatModel.Provider?
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
    @Ignore // TODO https://github.com/google/adk-java/issues/288
    public void template() throws IOException {
        var dotPromptLoader = new DotPromptLoader(rp, defaultLLM);
        var dotPrompt = dotPromptLoader.load(URI.create("classpath:/prompts/person.prompt.md"));
        dotPrompt.model = EchoModelProvider.ECHO_URI.toString();
        var agent = loader.convert(dotPrompt);
        var tester = new AgentTester(agent);

        tester.assertMarkdownResponseEquals(
                "This is a blog post about...",
                "Extract the requested information from the given text. If a piece"
                        + " of information is not present, omit that field from the"
                        + " output.\n"
                        + "\n"
                        + "Text: This is a blog post about...\n");
    }

    @Test
    // NOTA BENE: This test would actually PASS even if the @Test template() one FAILS;
    // this is because Gemini is "smart enough" to 'respect' the output schema in the prompt
    // EVEN if the prompt template actually is not passed through correctly (with {{text}}
    // replacement).
    public void person() throws IOException {
        if (!secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent()) return;

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
