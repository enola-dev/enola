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
package dev.enola.ai.adk.core;

import static dev.enola.ai.adk.core.Contents.replaceText;
import static dev.enola.ai.adk.core.InvocationContexts.replaceUserContent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.InvocationContext;
import com.google.adk.events.Event;

import dev.enola.common.function.CheckedFunction;

import io.reactivex.rxjava3.core.Flowable;

import java.io.IOException;
import java.util.List;

public class UserContentReplacingAgent extends BaseAgent {

    // TODO This doesn't actually really work as-is just yet; see the (failing)
    //   UserContentReplacingAgentTest... :-( See https://github.com/google/adk-java/issues/288.

    // TODO Raise ADK issue to simplify this; w.o. need for Contents, InvocationContexts

    private final CheckedFunction<String, String, IOException> userContentTextReplacer;
    private final BaseAgent delegateAgent;

    public UserContentReplacingAgent(
            String name,
            String description,
            CheckedFunction<String, String, IOException> userContentTextReplacer,
            BaseAgent delegateAgent
            // TODO List<Callbacks.BeforeAgentCallback> beforeAgentCallback,
            // List<Callbacks.AfterAgentCallback> afterAgentCallback
            ) {
        super(name, description, List.of(), List.of(), List.of());
        this.userContentTextReplacer = userContentTextReplacer;
        this.delegateAgent = delegateAgent;
    }

    // TODO Support BeforeAgentCallback & AfterAgentCallback? Share code with LlmAgent...

    // TODO Telemetry Tracer Scope... (see LLmAgent)

    @Override
    protected Flowable<Event> runAsyncImpl(InvocationContext parentContext) {
        try {
            var newContext = run(parentContext);
            return delegateAgent.runAsync(newContext);
        } catch (Throwable t) {
            return Flowable.error(t);
        }
    }

    @Override
    protected Flowable<Event> runLiveImpl(InvocationContext parentContext) {
        try {
            var newContext = run(parentContext);
            return delegateAgent.runLive(newContext);
        } catch (Throwable t) {
            return Flowable.error(t);
        }
    }

    private InvocationContext run(InvocationContext parentContext) throws IOException {
        var uc = parentContext.userContent();
        var replacedUserContent = replaceText(uc, userContentTextReplacer);
        var childContext = createInvocationContext(parentContext);
        return replaceUserContent(childContext, replacedUserContent);
    }

    // TODO Make this protected instead of private in BaseAgent, instead of copy/pasta it here:
    private InvocationContext createInvocationContext(InvocationContext parentContext) {
        InvocationContext invocationContext = InvocationContext.copyOf(parentContext);
        invocationContext.agent(this);
        // Check for branch to be truthy (not None, not empty string),
        if (parentContext.branch().filter(s -> !s.isEmpty()).isPresent()) {
            invocationContext.branch(parentContext.branch().get() + "." + name());
        }
        return invocationContext;
    }
}
