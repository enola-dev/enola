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
package dev.enola.ai.langchain4j.rag;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;

import java.util.concurrent.CompletableFuture;

/** Utility extension methods for {@link TokenStream}. */
public class TokenStreams {

    // TODO Upstream this class (and TestStreamingChatResponseHandler) to LangChain4j

    public static ChatResponse get(TokenStream tokenStream) {
        // https://github.com/langchain4j/langchain4j/issues/3770
        tokenStream.onPartialResponse(partialResponse -> {});

        CompletableFuture<ChatResponse> responseFuture = new CompletableFuture<>();
        tokenStream.onCompleteResponse(responseFuture::complete);
        tokenStream.onError(responseFuture::completeExceptionally);
        tokenStream.start();
        return responseFuture.join();
    }
}
