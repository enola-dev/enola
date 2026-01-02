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
package dev.enola.ai.dotagent;

import dev.enola.ai.dotprompt.DotPrompt;
import dev.enola.common.io.object.WithSchema;

import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Model (serializable e.g. as YAML) of Enola.dev's Dynamic Declarative Agents.
 *
 * <p>Partially by inspired by {@link DotPrompt}, but significantly expanding upon it to allow
 * declaring graphs of collaborating agents.
 */
public class AgentsModel extends WithSchema {

    // TODO Write the agents.proto corresponding to the (existing) agent.schema.yaml

    // TODO @JsonProperty("import") // cauz "import" is a reserved keyword in Java
    // TODO public final Set<URI> imports = new HashSet<>();

    public final List<Agent> agents = new ArrayList<>();

    public static class Agent extends DotPrompt {

        // public enum Visibility { user, internal }
        // TODO public Visibility visibility = Visibility.user;

        /**
         * One-line description of the agent's capability. The model uses this to determine whether
         * to delegate control to the agent.
         */
        public @Nullable String description;

        /** Instruction; see schema for full description. */
        // TODO How does instruction related to DotPrompt's template => prompt ?!
        public @Nullable String instruction;

        // TODO public final List<String> sequence = new ArrayList<>();

        // TODO public final Set<String> parallel = new HashSet<>();

        // TODO public final Set<String> loop = new HashSet<>();
    }
}
