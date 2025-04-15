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
package dev.enola.ai.langchain4j;

import com.google.common.util.concurrent.Futures;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;

import java.util.concurrent.CompletableFuture;

public class TestStreamingChatResponseHandler implements StreamingChatResponseHandler {

    // TODO Propose contribution of this class to upstream LangChain4j

    private final CompletableFuture<ChatResponse> responseFuture = new CompletableFuture<>();

    @Override
    public void onPartialResponse(String partialResponse) {
        // IGNORE.
    }

    @Override
    public void onCompleteResponse(ChatResponse completeResponse) {
        responseFuture.complete(completeResponse);
    }

    @Override
    public void onError(Throwable error) {
        responseFuture.completeExceptionally(error);
    }

    public ChatResponse awaitChatResponse() {
        return Futures.getUnchecked(responseFuture);
    }
}
