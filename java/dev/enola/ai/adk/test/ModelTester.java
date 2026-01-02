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
package dev.enola.ai.adk.test;

import com.google.adk.models.BaseLlm;
import com.google.adk.models.LlmRequest;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import java.util.List;

public class ModelTester {

    // TODO Abandon and replace this with similar (!) AgentTester(BaseLlm model) ?

    // TODO Re-write this as a Truth Subject?

    private final BaseLlm llm;

    public ModelTester(BaseLlm llm) {
        this.llm = llm;
    }

    public void assertTextResponseContains(
            String prompt, String... responseMustContainAtLeastOneOf) {
        var response = invoke(prompt);
        Asserter.assertTextResponseContainsAny(response, responseMustContainAtLeastOneOf);
    }

    public void assertTextResponseEquals(String prompt, String responseMustBeEqualTo) {
        var response = invoke(prompt);
        Asserter.assertTextResponseEquals(response, responseMustBeEqualTo);
    }

    // TODO Support expected MediaType of response; e.g. image, or JSON!
    private String invoke(String prompt) {
        var content = Content.fromParts(Part.fromText(prompt));
        var request = LlmRequest.builder().contents(List.of(content)).build();
        var response = llm.generateContent(request, false).blockingFirst();
        var text = response.content().orElseThrow().text();
        if (text == null) throw new AssertionError("Reponse text is null");
        return text;
    }
}
