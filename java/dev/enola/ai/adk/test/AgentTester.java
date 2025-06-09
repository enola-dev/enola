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

import com.google.adk.agents.BaseAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;

public class AgentTester {

    // TODO Maybe re-write this as a Truth Subject?

    private final BaseAgent agent;

    public AgentTester(BaseAgent agent) {
        this.agent = agent;
    }

    public void assertTextResponseContains(String prompt, String responseMustContain) {
        String userID = "tester";
        InMemoryRunner runner = new InMemoryRunner(agent);
        Session session = runner.sessionService().createSession(agent.name(), userID).blockingGet();
        try {
            Content userMsg = Content.fromParts(Part.fromText(prompt));

            Flowable<Event> eventsFlow = runner.runAsync(userID, session.id(), userMsg);
            TestSubscriber<Event> testSubscriber = TestSubscriber.create();
            eventsFlow.subscribe(testSubscriber);
            testSubscriber.assertNoErrors();
            testSubscriber.assertComplete();
            var events = testSubscriber.values();

            var sb = new StringBuilder();
            for (var event : events) {
                if (event.content().isPresent()) {
                    var content = event.content().get();
                    if (content.text() != null) sb.append(content.text());
                }
            }
            for (var event : events) {
                if (event.content().isPresent()) {
                    var content = event.content().get();
                    if (content.parts().isPresent()) {
                        for (var part : content.parts().get()) {
                            part.text().ifPresent(sb::append);
                        }
                    }
                }
            }
            var response = sb.toString();
            if (!response.contains(responseMustContain))
                throw new AssertionError(response + " does not contain: " + responseMustContain);

        } finally {
            runner.sessionService().closeSession(session);
            // TODO Propose deleteSession(Sesssion session)
            runner.sessionService()
                    .deleteSession(session.appName(), session.userId(), session.id());
        }
    }
}
