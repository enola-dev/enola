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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.ai.adk.core.Contents.replaceText;

import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.common.function.CheckedFunction;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class ContentsTest {

    CheckedFunction<String, String, IOException> R = s -> s + "bar";

    @Test
    public void emptyOptional() throws IOException {
        assertThat(replaceText(Optional.empty(), R)).isEmpty();
    }

    @Test
    public void emptyContent() throws IOException {
        var emptyContent = Content.fromParts();
        assertThat(Contents.replaceText(emptyContent, R)).isEqualTo(emptyContent);
    }

    @Test
    public void text1() throws IOException {
        assertThat(Contents.replaceText(Content.fromParts(Part.fromText("foo")), R))
                .isEqualTo(Content.fromParts(Part.fromText("foobar")));
    }

    @Test
    public void text2() throws IOException {
        assertThat(
                        Contents.replaceText(
                                Content.fromParts(Part.fromText("foo"), Part.fromText("xyz")), R))
                .isEqualTo(Content.fromParts(Part.fromText("foobar"), Part.fromText("xyzbar")));
    }

    @Test
    public void uri() throws IOException {
        var uriContent = Content.fromParts(Part.fromUri("http://example.org", "text.html"));
        assertThat(Contents.replaceText(uriContent, R)).isEqualTo(uriContent);
    }

    @Test
    public void uriAndText() throws IOException {
        var uriPart = Part.fromUri("http://example.org", "text.html");
        assertThat(Contents.replaceText(Content.fromParts(uriPart, Part.fromText("foo")), R))
                .isEqualTo(Content.fromParts(uriPart, Part.fromText("foobar")));
    }
}
