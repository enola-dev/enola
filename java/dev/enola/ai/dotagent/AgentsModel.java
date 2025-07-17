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
package dev.enola.ai.dotagent;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.enola.ai.dotprompt.DotPrompt;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.*;

/**
 * Model (serializable e.g. as YAML) of Enola.dev's Dynamic Declarative Agents.
 *
 * <p>Partially by inspired by {@link DotPrompt}, but significantly expanding upon it to allow
 * declaring graphs of collaborating agents.
 */
public class AgentsModel {

    // TODO Write the corresponding agents.schema.json and agents.proto

    @JsonProperty("import") // cauz "import" is a reserved keyword in Java
    public final Set<URI> imports = new HashSet<>();

    public final Map<String, Agent> agents = new HashMap<>();

    public static class Agent extends DotPrompt {

        public enum Visibility {
            user,
            internal
        }

        public Visibility visibility = Visibility.user;

        public @Nullable String description;

        public final List<String> sequence = new ArrayList<>();

        public final Set<String> parallel = new HashSet<>();

        public final Set<String> loop = new HashSet<>();
    }
}
