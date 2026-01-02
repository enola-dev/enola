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

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.InvocationContext;
import com.google.adk.events.Event;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import io.reactivex.rxjava3.core.Flowable;

import java.util.List;
import java.util.function.Supplier;

public class MockAgent extends BaseAgent {

    // https://github.com/google/adk-java/blob/main/core/src/test/java/com/google/adk/testing/TestBaseAgent.java
    //   is similar to this, but it's in src/test and thus not available...
    //   TODO Propose a related refactoring!

    private final Supplier<Flowable<Event>> eventSupplier;
    private String reply;

    public MockAgent(String reply) {
        super("Mock", "Mock Agent for Testing", List.of(), null, null);
        this.reply = reply;

        this.eventSupplier =
                () ->
                        Flowable.just(
                                Event.builder()
                                        .id(Event.generateEventId())
                                        .author("MockAgent")
                                        .content(Content.fromParts(Part.fromText(this.reply)))
                                        .build());
    }

    public void replyWith(String text) {
        this.reply = text;
    }

    @Override
    protected Flowable<Event> runAsyncImpl(InvocationContext invocationContext) {
        return eventSupplier.get();
    }

    @Override
    protected Flowable<Event> runLiveImpl(InvocationContext invocationContext) {
        return eventSupplier.get();
    }
}
