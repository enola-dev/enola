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
package dev.enola.tool.todo;

import com.google.auto.value.AutoBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import dev.enola.data.id.UUID_IRI;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ToDo(
        // TODO implements Identifiable<URI>
        URI id,
        String title,
        Optional<String> description,
        ImmutableList<String> tags,
        ImmutableMap<String, String> attributes,
        Optional<Instant> created,
        Optional<Instant> completed,
        Optional<Byte> priority,

        /** Assignee URI; e.g. <code>mailto:user@example.org</code>. */
        Optional<URI> assignee,

        /** Parent ToDo ID; e.g. a "Project". */
        // TODO Add other more flexible relationships, e.g. "depends on", "related to" etc.
        // TODO public Set<URI> children = new HashSet<>();
        Optional<URI> parent

        // TODO Discussion, like in a bug tracker, with comments by users etc.

        // TODO public Set<URI> attachments = new HashSet<>();
        ) {

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isCompleted() {
        return completed.isPresent();
    }

    public static Builder builder() {
        return new AutoBuilder_ToDo_Builder();
    }

    public Builder toBuilder() {
        return builder().from(this);
    }

    @AutoBuilder(callMethod = "create")
    public abstract static class Builder {
        public abstract Builder id(URI id);

        public abstract Builder title(String title);

        public abstract Builder description(String description);

        public abstract Builder tags(List<String> tags);

        public abstract Builder attributes(Map<String, String> attributes);

        public abstract Builder created(Instant created);

        public abstract Builder completed(Instant completed);

        public abstract Builder priority(Byte priority);

        public abstract Builder assignee(URI assignee);

        public abstract Builder parent(URI parent);

        public Builder from(ToDo todo) {
            id(todo.id());
            title(todo.title());
            todo.description().ifPresent(this::description);
            tags(todo.tags());
            attributes(todo.attributes());
            todo.created().ifPresent(this::created);
            todo.completed().ifPresent(this::completed);
            todo.priority().ifPresent(this::priority);
            todo.assignee().ifPresent(this::assignee);
            todo.parent().ifPresent(this::parent);
            return this;
        }

        public abstract ToDo build();
    }

    static ToDo create(
            @org.jspecify.annotations.Nullable URI id,
            String title,
            @org.jspecify.annotations.Nullable Optional<String> description,
            @org.jspecify.annotations.Nullable List<String> tags,
            @org.jspecify.annotations.Nullable Map<String, String> attributes,
            @org.jspecify.annotations.Nullable Optional<Instant> created,
            @org.jspecify.annotations.Nullable Optional<Instant> completed,
            @org.jspecify.annotations.Nullable Optional<Byte> priority,
            @org.jspecify.annotations.Nullable Optional<URI> assignee,
            @org.jspecify.annotations.Nullable Optional<URI> parent) {

        if (title == null) throw new IllegalStateException("Task title is required");
        if (id == null) id = new UUID_IRI().toURI();

        if (created == null || created.isEmpty()) {
            created = Optional.of(Instant.now());
        }

        return new ToDo(
                id,
                title,
                description == null ? Optional.empty() : description,
                tags == null ? ImmutableList.of() : ImmutableList.copyOf(tags),
                attributes == null ? ImmutableMap.of() : ImmutableMap.copyOf(attributes),
                created,
                completed == null ? Optional.empty() : completed,
                priority == null ? Optional.empty() : priority,
                assignee == null ? Optional.empty() : assignee,
                parent == null ? Optional.empty() : parent);
    }
}
