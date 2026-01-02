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

import com.google.adk.models.LlmResponse;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import io.reactivex.rxjava3.core.Flowable;

import java.util.List;

public class MockModel extends TestLlm {

    public MockModel(String reply) {
        super(() -> create(reply));
    }

    private static Flowable<LlmResponse> create(String textReply) {
        var replyParts = List.of(Part.fromText(textReply));
        var content = Content.builder().role("model").parts(replyParts).build();
        var llmResponse = LlmResponse.builder().content(content).build();
        return Flowable.just(llmResponse);
    }
}
