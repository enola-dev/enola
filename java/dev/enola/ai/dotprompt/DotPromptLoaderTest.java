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
package dev.enola.ai.dotprompt;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.markdown.Markdown;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class DotPromptLoaderTest {

    @Test
    public void testExample1() throws IOException {
        var llmURI = URI.create("TheLLM");
        var loader = new DotPromptLoader(new ClasspathResource.Provider(), llmURI);
        var dotPrompt = loader.load(URI.create("classpath:/prompts/summarize.prompt.md"));

        assertThat(dotPrompt.name).isEqualTo("summarize");
        assertThat(dotPrompt.model).isEqualTo(llmURI.toString());
        // TODO assertThat(dotPrompt.input.schema.get("text")).isEqualTo("string");

        var response = dotPrompt.template.apply(Map.of("text", "This is a blog post about..."));
        response = Markdown.canonicalize(response);
        assertThat(response)
                .isEqualTo(
                        Markdown.canonicalize(
                                "Extract the requested information from the given text. If a piece"
                                        + " of information is not present, omit that field from the"
                                        + " output.\n"
                                        + "\n"
                                        + "Text: This is a blog post about...\n"));
    }
}
