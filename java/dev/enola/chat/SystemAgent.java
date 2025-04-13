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

import com.google.common.collect.ImmutableList;

import dev.enola.identity.Subject;

public class SystemAgent extends AbstractAgent {

    private final ImmutableList<Agent> agents;

    public SystemAgent(Switchboard pbx) {
        super(_subject(), pbx);
        this.agents = ImmutableList.of();
    }

    public SystemAgent(Switchboard pbx, Agent... agents) {
        super(_subject(), pbx);
        this.agents = ImmutableList.copyOf(agents);
        // Avoid potential @system circular reference loops
        if (this.agents.contains(this)) throw new IllegalArgumentException();
    }

    @Override
    public void accept(Message message) {
        var content = message.content();
        if (content.startsWith("@")) {
            var end = content.indexOf(' ');
            var at = content.substring(1, end).trim();
            // First, check for (unique!) Agent IRI match
            for (var agent : agents) {
                if (agent.subject().iri().equals(at)) {
                    agent.accept(message);
                    return;
                }
            }
            // Next, if non found, check for first (non-unique!) Agent Label match
            var atLowerCase = at.toLowerCase();
            for (var agent : agents) {
                var label = agent.subject().label();
                if (label != null && label.toLowerCase().equals(atLowerCase)) {
                    agent.accept(message);
                    return;
                }
            }
        }
    }

    private static Subject _subject() {
        return tbf.create(Subject.Builder.class, Subject.class)
                .iri("https://enola.dev/system")
                .label("System")
                .comment("Enola.dev System Agent.")
                .build();
    }
}
