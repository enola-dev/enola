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

import static com.google.common.truth.Truth.assertThat;

import dev.enola.ai.iri.OllamaModelProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.ai.langchain4j.ChatModelProviders;
import dev.enola.common.Net;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.UnavailableSecretManager;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import org.junit.Test;

public class LearnLangChain4jRAGTest {

    SecretManager secretManager = new UnavailableSecretManager();
    Provider<StreamingChatModel> p = new ChatModelProviders(secretManager);

    interface Assistant {
        TokenStream chat(@UserMessage String userMessage);
    }

    @Test
    public void tony() {
        if (!Net.portAvailable(11434)) return;

        // TODO Use io.Resource framework; support Globs etc.
        var doc = ClassPathDocumentLoader.loadDocument("rag/tony.md");

        // TODO AI URI for Embedding Stores
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(doc, embeddingStore);

        var model = p.get(OllamaModelProvider.GEMMA3_1B);

        // TODO Get rid of useless interface Assistant ...
        var assistant =
                AiServices.builder(Assistant.class)
                        .streamingChatModel(model)
                        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                        .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                        .build();

        // TODO var answer = new TestStreamingChatResponseHandler();
        var answer = assistant.chat("Which country is Tony from?");
        // assertThat(answer.awaitChatResponse().aiMessage().text()).contains("Switzerland");
        assertThat(TokenStreams.get(answer).aiMessage().text()).contains("Swiss"); // "Switzerland"?
    }
}
