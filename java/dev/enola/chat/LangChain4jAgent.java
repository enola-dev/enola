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
package dev.enola.chat;

import dev.enola.ai.langchain4j.ChatLanguageModelProvider;
import dev.enola.ai.langchain4j.TestStreamingChatResponseHandler;
import dev.enola.identity.Subject;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;

import java.net.URI;

public class LangChain4jAgent extends AbstractAgent {

    private final StreamingChatLanguageModel lm;

    protected LangChain4jAgent(URI llmURL, Switchboard pbx) {
        super(
                tbf.create(Subject.Builder.class, Subject.class)
                        .iri(llmURL.toString())
                        .label("LLM")
                        .comment(llmURL.toString())
                        .build(),
                pbx);
        lm = new ChatLanguageModelProvider().get(llmURL);
    }

    @Override
    public void accept(Message message) {
        // Avoid it talking to itself endlessly... ROTFL!
        if (message.from().equals(subject())) return;

        // TODO Support streaming LLM partial responses into Chat
        // TODO https://docs.langchain4j.dev/tutorials/chat-memory
        var handler = new TestStreamingChatResponseHandler();
        lm.chat(message.content(), handler);
        reply(message, handler.awaitChatResponse().aiMessage().text());
    }
}
