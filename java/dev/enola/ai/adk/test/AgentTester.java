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
import com.google.common.base.Strings;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.common.markdown.Markdown;
import dev.enola.common.yamljson.JSON;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import org.junit.ComparisonFailure;

import java.util.Arrays;

public class AgentTester {

    // TODO Support Multiple root agents?!

    // TODO Re-write this as a Truth Subject?

    private final BaseAgent agent;

    public AgentTester(BaseAgent agent) {
        this.agent = agent;
    }

    public void assertTextResponseContains(
            String prompt, String... responseMustContainAtLeastOneOf) {
        var response = invoke(prompt);

        for (var responseMustContain : responseMustContainAtLeastOneOf)
            if (response.toLowerCase().contains(responseMustContain.toLowerCase())) return;

        throw new AssertionError(
                response
                        + " does not contain any of: "
                        + Arrays.toString(responseMustContainAtLeastOneOf));
    }

    public void assertTextResponseEquals(String prompt, String responseMustBeEqualTo) {
        var response = invoke(prompt);
        if (!response.equalsIgnoreCase(responseMustBeEqualTo))
            throw new ComparisonFailure("!equalsIgnoreCase", responseMustBeEqualTo, response);
    }

    public void assertJsonResponseEquals(String prompt, String expectedJSON) {
        var response = invoke(prompt);
        var actualCanonicalFormattedJSON = JSON.canonicalize(response, true);
        var expectedCanonicalFormattedJSON = JSON.canonicalize(expectedJSON, true);

        if (!actualCanonicalFormattedJSON.equalsIgnoreCase(expectedCanonicalFormattedJSON))
            throw new ComparisonFailure(
                    "JSON is not equal",
                    expectedCanonicalFormattedJSON,
                    actualCanonicalFormattedJSON);
    }

    public void assertMarkdownResponseEquals(String prompt, String expectedMarkdown) {
        var response = invoke(prompt);
        var actualCanonicalFormattedMarkdown = Markdown.canonicalize(response);
        var expectedCanonicalFormattedMarkdown = Markdown.canonicalize(expectedMarkdown);

        if (!actualCanonicalFormattedMarkdown.equalsIgnoreCase(expectedCanonicalFormattedMarkdown))
            throw new ComparisonFailure(
                    "Markdown is not equal",
                    expectedCanonicalFormattedMarkdown,
                    actualCanonicalFormattedMarkdown);
    }

    private String invoke(String prompt) {
        String userID = "tester";
        InMemoryRunner runner = new InMemoryRunner(agent);
        var sessionService = runner.sessionService();
        Session session = sessionService.createSession(agent.name(), userID).blockingGet();
        try {
            Content userMsg = Content.fromParts(Part.fromText(prompt));
            Flowable<Event> eventsFlow = runner.runAsync(userID, session.id(), userMsg);
            // TestSubscriber<Event> testSubscriber = TestSubscriber.create();
            // eventsFlow.subscribe(testSubscriber);
            // testSubscriber.assertNoErrors();runConfigBuilder
            // testSubscriber.assertComplete();
            // var events = testSubscriber.values();
            var events =
                    eventsFlow
                            .subscribeOn(Schedulers.trampoline())
                            .observeOn(Schedulers.trampoline())
                            .blockingIterable();

            var sb = new StringBuilder();
            for (var event : events) {
                if (event.content().isPresent()) {
                    var content = event.content().get();
                    var text = content.text();
                    if (!Strings.isNullOrEmpty(text)) sb.append(text);
                    // TODO Re-review more closely if this is even needed?!
                    else if (content.parts().isPresent()) {
                        for (var part : content.parts().get()) {
                            part.text().ifPresent(sb::append);
                        }
                    }
                }
            }
            return sb.toString();

        } finally {
            sessionService.closeSession(session);
            // TODO Propose deleteSession(Sesssion session)
            sessionService.deleteSession(session.appName(), session.userId(), session.id());
        }
    }
}
