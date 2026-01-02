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

import com.google.adk.agents.BaseAgent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public final class Agents {

    public static ImmutableMap<String, BaseAgent> toMap(Iterable<BaseAgent> agents) {
        // NB: ImmutableMap.Builder will raise an error when adding an agent with the same name as
        // another
        var builder =
                ImmutableMap.<String, BaseAgent>builderWithExpectedSize(Iterables.size(agents));
        agents.forEach(agent -> builder.put(agent.name(), agent));
        return builder.build();
    }

    private Agents() {}
}
