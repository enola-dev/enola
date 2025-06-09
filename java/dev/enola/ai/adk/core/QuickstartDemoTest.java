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
package dev.enola.ai.adk.core;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.ai.iri.GoogleModelProvider.GOOGLE_AI_API_KEY_SECRET_NAME;

import com.google.adk.events.Event;
import com.google.adk.models.BaseLlm;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.ai.adk.iri.BaseLlmProviders;
import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;

import org.junit.Test;

import java.io.IOException;

public class QuickstartDemoTest {

    SecretManager secretManager = new TestSecretManager();
    Provider<BaseLlm> p = new BaseLlmProviders(secretManager);

    @Test
    public void test() throws IOException {
        if (!secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent()) return;
        var agent = QuickstartDemo.initAgent(p.get(GoogleModelProvider.FLASH));

        // TODO Factor all this "test wrapper code" out into an AutoCloseable utility class
        String userID = "tester";
        InMemoryRunner runner = new InMemoryRunner(agent);
        Session session = runner.sessionService().createSession(agent.name(), userID).blockingGet();
        try {
            Content userMsg = Content.fromParts(Part.fromText("What's the weather in New York?"));

            Flowable<Event> eventsFlow = runner.runAsync(userID, session.id(), userMsg);
            // eventsFlow.filter() / map() ?
            TestSubscriber<Event> testSubscriber = TestSubscriber.create();
            eventsFlow.subscribe(testSubscriber);
            testSubscriber.assertNoErrors();
            testSubscriber.assertComplete();
            var events = testSubscriber.values();
            assertThat(events).hasSize(3);
            assertThat(events.get(2).content().get().parts().get().get(0).text().get())
                    .isEqualTo(QuickstartDemo.NYC_WEATHER);

            // TODO "What's the current time in Zürich?"

        } finally {
            runner.sessionService().closeSession(session);
            // TODO Propose deleteSession(Sesssion session)
            runner.sessionService()
                    .deleteSession(session.appName(), session.userId(), session.id());
        }
    }
}
