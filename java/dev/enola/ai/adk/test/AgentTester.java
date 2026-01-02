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
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.models.BaseLlm;
import com.google.adk.tools.BaseTool;
import com.google.common.base.Strings;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.ai.adk.core.UserSessionRunner;
import dev.enola.common.markdown.Markdown;
import dev.enola.common.yamljson.JSON;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import org.junit.ComparisonFailure;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AgentTester {

    // TODO Support Multiple root agents?!

    // TODO Re-write this as a Truth Subject?

    // TODO Avoid (almost) copy/paste in ModelTester

    private final UserSessionRunner runner;

    public AgentTester(BaseAgent agent) {
        this.runner = new UserSessionRunner("tester", agent);
    }

    public AgentTester(BaseLlm model) {
        this(LlmAgent.builder().name(model.model()).model(model).build());
    }

    public AgentTester(BaseLlm model, BaseTool... tools) {
        // The cast to Object[] is needed to avoid a compiler warning about ambiguous varargs.
        this(LlmAgent.builder().name(model.model()).model(model).tools((Object[]) tools).build());
    }

    public AgentTester(BaseLlm model, Map<String, BaseTool> tools) {
        this(
                LlmAgent.builder()
                        .name(model.model())
                        .model(model)
                        .tools(List.copyOf(tools.values()))
                        .build());
    }

    public void assertTextResponseContains(String prompt, String contains) {
        var response = invoke(prompt);
        Asserter.assertTextResponseContainsAny(response, new String[] {contains});
    }

    public void assertTextResponseContainsAny(String prompt, String first, String... rest) {
        var response = invoke(prompt);
        var all = Stream.concat(Stream.of(first), Stream.of(rest)).toArray(String[]::new);
        Asserter.assertTextResponseContainsAny(response, all);
    }

    public void assertTextResponseContainsAll(String prompt, String first, String... rest) {
        var response = invoke(prompt);
        var all = Stream.concat(Stream.of(first), Stream.of(rest)).toArray(String[]::new);
        Asserter.assertTextResponseContainsAll(response, all);
    }

    // TODO This is quasi useless, no?
    public void assertTextResponseEquals(String prompt, String responseMustBeEqualTo) {
        var response = invoke(prompt);
        Asserter.assertTextResponseEquals(response, responseMustBeEqualTo);
    }

    // TODO This doesn't actually work... yet; we need to invoke with expected MediaType JSON
    public void assertJsonResponseEquals(String prompt, String expectedJSON) {
        var response = invoke(prompt);
        var expectedCanonicalFormattedJSON = JSON.canonicalize(expectedJSON, true);
        String actualCanonicalFormattedJSON;
        try {
            actualCanonicalFormattedJSON = JSON.canonicalize(response, true);
        } catch (IllegalArgumentException e) {
            actualCanonicalFormattedJSON = response; // not even valid JSON
        }

        if (!actualCanonicalFormattedJSON.equals(expectedCanonicalFormattedJSON))
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

    // TODO Support expected MediaType of response; e.g. image, or JSON!
    private String invoke(String prompt) {
        Content userMsg = Content.fromParts(Part.fromText(prompt));
        Flowable<Event> eventsFlow = runner.runAsync(userMsg);
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
    }
}
