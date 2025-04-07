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

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;

// TODO Switch from ChatLanguageModel to StreamingChatLanguageModel
public class TestChatLanguageModel implements ChatLanguageModel {
    private final String reply;

    public TestChatLanguageModel(String reply) {
        this.reply = reply;
    }

    @Override
    public String chat(String userMessage) {
        return ChatLanguageModel.super.chat(userMessage);
    }

    @Override
    public ChatResponse doChat(ChatRequest chatRequest) {
        var response = ChatResponse.builder();
        response.aiMessage(AiMessage.builder().text(reply).build());
        return response.build();
    }
}
