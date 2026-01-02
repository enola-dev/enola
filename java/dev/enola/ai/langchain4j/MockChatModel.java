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
package dev.enola.ai.langchain4j;

import dev.enola.ai.iri.MockModelProvider;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.FinishReason;

import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;

public class MockChatModel implements StreamingChatModel {

    private final String reply;

    public MockChatModel(String reply) {
        this.reply = reply;
    }

    @Override
    public void doChat(ChatRequest chatRequest, StreamingChatResponseHandler handler) {
        handler.onCompleteResponse(
                ChatResponse.builder()
                        .aiMessage(AiMessage.builder().text(reply).build())
                        .finishReason(FinishReason.STOP)
                        .build());
    }

    public static class Provider extends MockModelProvider<StreamingChatModel> // skipcq: JAVA-E0169
            implements ChatModelProvider { // skipcq: JAVA-E0169

        @Override
        public Optional<StreamingChatModel> optional(URI uri)
                throws IllegalArgumentException, UncheckedIOException {
            if (SCHEME.equalsIgnoreCase(uri.getScheme())) {
                var reply = uri.getSchemeSpecificPart();
                return Optional.of(new MockChatModel(reply));
            } else return Optional.empty();
        }
    }
}
