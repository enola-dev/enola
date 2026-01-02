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
import com.google.adk.models.BaseLlmConnection;
import com.google.adk.models.LlmRequest;
import com.google.adk.models.LlmResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import io.reactivex.rxjava3.core.Flowable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

// Copy/pasted from
// https://github.com/google/adk-java/blob/cb95b56b280c51dc67716fc17e0ded92d0d2c4f5/core/src/test/java/com/google/adk/testing/TestLlm.java#L1
// TODO Replace with upstream; see https://github.com/google/adk-java/issues/289 (make it non-final)
class TestLlm extends BaseLlm {
    private final List<LlmRequest> llmRequests = Collections.synchronizedList(new ArrayList<>());

    private final List<LlmResponse> responseSequence;
    private final AtomicInteger responseIndex = new AtomicInteger(0);

    // TODO Find a better design than removing final and making protected instead of private?
    protected Supplier<Flowable<LlmResponse>> responsesSupplier;

    public TestLlm(List<LlmResponse> responses) {
        super("test-llm");
        this.responseSequence =
                (responses == null) ? ImmutableList.of() : ImmutableList.copyOf(responses);
        this.responsesSupplier = null;
    }

    public TestLlm(Supplier<Flowable<LlmResponse>> responsesSupplier) {
        super("test-llm");
        this.responsesSupplier = responsesSupplier;
        this.responseSequence = null;
    }

    @Override
    public Flowable<LlmResponse> generateContent(LlmRequest llmRequest, boolean stream) {
        llmRequests.add(llmRequest);

        if (this.responseSequence != null) {
            // Sequential discrete response mode
            int currentIndex = responseIndex.getAndIncrement();
            if (currentIndex < responseSequence.size()) {
                LlmResponse nextResponse = responseSequence.get(currentIndex);
                return Flowable.just(nextResponse);
            } else {
                return Flowable.error(
                        new NoSuchElementException(
                                "TestLlm (List mode) out of responses. Requested response for LLM"
                                        + " call "
                                        + llmRequests.size()
                                        + " (index "
                                        + currentIndex
                                        + ") but only "
                                        + responseSequence.size()
                                        + " were configured."));
            }
        } else if (this.responsesSupplier != null) {
            // Legacy/streaming supplier mode
            return responsesSupplier.get();
        } else {
            // Should not happen if constructors are used properly
            return Flowable.error(
                    new IllegalStateException("TestLlm not initialized with responses."));
        }
    }

    @Override
    public BaseLlmConnection connect(LlmRequest llmRequest) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ImmutableList<LlmRequest> getRequests() {
        return ImmutableList.copyOf(llmRequests);
    }

    public LlmRequest getLastRequest() {
        return Iterables.getLast(llmRequests);
    }
}
