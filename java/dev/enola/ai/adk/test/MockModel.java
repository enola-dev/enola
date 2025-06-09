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
package dev.enola.ai.adk.test;

import com.google.adk.models.BaseLlm;
import com.google.adk.models.BaseLlmConnection;
import com.google.adk.models.LlmRequest;
import com.google.adk.models.LlmResponse;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import io.reactivex.rxjava3.core.Flowable;

import java.util.List;

public class MockModel extends BaseLlm {

    // https://github.com/google/adk-java/blob/main/core/src/test/java/com/google/adk/testing/TestLlm.java
    //   is similar to this, but it's in src/test and thus not available...
    //   TODO Propose a related refactoring!

    private final String reply;

    public MockModel(String reply) {
        super("Mock");
        this.reply = reply;
    }

    @Override
    public Flowable<LlmResponse> generateContent(LlmRequest llmRequest, boolean stream) {
        var parts = List.of(Part.fromText(reply));
        var content = Content.builder().role("model").parts(parts).build();
        return Flowable.just(LlmResponse.builder().content(content).build());
    }

    @Override
    public BaseLlmConnection connect(LlmRequest llmRequest) {
        throw new IllegalStateException("TODO");
    }
}
