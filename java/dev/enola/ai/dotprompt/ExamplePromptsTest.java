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
package dev.enola.ai.dotprompt;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.object.jackson.YamlObjectReaderWriter;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MarkdownResource;

import org.junit.Test;

import java.io.IOException;

public class ExamplePromptsTest {

    @Test
    public void testExample1() throws IOException {
        // TODO Move some of this code into another class later...
        var md = new MarkdownResource(new ClasspathResource("example1.prompt.md"));
        assertThat(md.frontMatter().charSource().isEmpty()).isFalse();

        var reader = new YamlObjectReaderWriter();
        var dotPromptFrontmatter = reader.read(md.frontMatter(), DotPrompt.class);
        assertThat(dotPromptFrontmatter.model).isEqualTo("googleai/gemini-1.5-pro");
    }
}
