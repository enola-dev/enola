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
package dev.enola.ai.adk.web;

import com.google.adk.agents.BaseAgent;
import com.google.adk.web.AgentLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// TODO Upstream this (it's package-local there)
public class AgentStaticLoader implements AgentLoader {
    private final ImmutableMap<String, BaseAgent> agents;

    public AgentStaticLoader(BaseAgent... agents) {
        this(Arrays.stream(agents));
    }

    public AgentStaticLoader(Iterable<BaseAgent> agents) {
        this(StreamSupport.stream(agents.spliterator(), false));
    }

    public AgentStaticLoader(Stream<BaseAgent> agents) {
        this.agents =
                agents.collect(ImmutableMap.toImmutableMap(BaseAgent::name, Function.identity()));
    }

    public ImmutableList<String> listAgents() {
        return this.agents.keySet().stream().collect(ImmutableList.toImmutableList());
    }

    public BaseAgent loadAgent(String name) {
        if (name != null && !name.trim().isEmpty()) {
            BaseAgent agent = this.agents.get(name);
            if (agent == null) {
                throw new NoSuchElementException("Agent not found: " + name);
            } else {
                return agent;
            }
        } else {
            throw new IllegalArgumentException("Agent name cannot be null or empty");
        }
    }
}
