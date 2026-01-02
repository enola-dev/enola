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

import com.google.common.collect.ImmutableList;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.common.function.CheckedFunction;
import dev.enola.common.function.Optionals;

import java.io.IOException;
import java.util.Optional;

final class Contents {
    // != com.google.adk.flows.llmflows.Contents

    public static Content replaceText(
            Content content, CheckedFunction<String, String, IOException> replacer)
            throws IOException {
        if (content.parts().isEmpty()) return content;

        var partsBuilder = ImmutableList.<Part>builder();
        for (var part : content.parts().get()) {
            if (part.text().isEmpty()) partsBuilder.add(part);
            else {
                var replaced = replacer.apply(part.text().get());
                partsBuilder.add(Part.fromText(replaced));
            }
        }

        var contentBuilder = Content.builder();
        content.role().ifPresent(contentBuilder::role);
        contentBuilder.parts(partsBuilder.build());
        return contentBuilder.build();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<Content> replaceText(
            Optional<Content> content, CheckedFunction<String, String, IOException> replacer)
            throws IOException {
        return Optionals.map(content, c -> replaceText(c, replacer));
    }

    private Contents() {}
}
