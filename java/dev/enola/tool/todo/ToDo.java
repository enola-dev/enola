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

import dev.enola.data.id.UUID_IRI;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ToDo(
        // TODO implements Identifiable<URI>
        URI id,
        String title,
        @Nullable String description,
        List<String> tags,
        Map<String, String> attributes,
        @Nullable Instant created,
        @Nullable Instant modified,
        @Nullable Instant completed,
        @Nullable Boolean isCompleted,
        @Nullable Byte priority,

        /** Assignee URI; e.g. <code>mailto:user@example.org</code>. */
        @Nullable URI assignee,

        /** Parent ToDo ID; e.g. a "Project". */
        // TODO Add other more flexible relationships, e.g. "depends on", "related to" etc.
        // TODO public Set<URI> children = new HashSet<>();
        @Nullable URI parent

        // TODO Discussion, like in a bug tracker, with comments by users etc.

        // TODO public Set<URI> attachments = new HashSet<>();
        ) {

    public static Builder builder() {
        return new AutoBuilder_ToDo_Builder();
    }

    public Builder toBuilder() {
        return builder().from(this);
    }

    @AutoBuilder(callMethod = "create")
    public abstract static class Builder {
        public abstract Builder id(@Nullable URI id);

        public abstract Builder title(String title);

        public abstract Builder description(@Nullable String description);

        public abstract Builder tags(@Nullable List<String> tags);

        public abstract Builder attributes(@Nullable Map<String, String> attributes);

        public abstract Builder created(@Nullable Instant created);

        public abstract Builder modified(@Nullable Instant modified);

        public abstract Builder completed(@Nullable Instant completed);

        public abstract Builder isCompleted(@Nullable Boolean isCompleted);

        public abstract Builder priority(@Nullable Byte priority);

        public abstract Builder assignee(@Nullable URI assignee);

        public abstract Builder parent(@Nullable URI parent);

        public Builder from(ToDo todo) {
            return id(todo.id())
                    .title(todo.title())
                    .description(todo.description())
                    .tags(todo.tags())
                    .attributes(todo.attributes())
                    .created(todo.created())
                    .modified(todo.modified())
                    .completed(todo.completed())
                    .isCompleted(todo.isCompleted())
                    .priority(todo.priority())
                    .assignee(todo.assignee())
                    .parent(todo.parent());
        }

        public abstract ToDo build();
    }

    static ToDo create(
            @Nullable URI id,
            String title,
            @Nullable String description,
            @Nullable List<String> tags,
            @Nullable Map<String, String> attributes,
            @Nullable Instant created,
            @Nullable Instant modified,
            @Nullable Instant completed,
            @Nullable Boolean isCompleted,
            @Nullable Byte priority,
            @Nullable URI assignee,
            @Nullable URI parent) {

        if (title == null) throw new IllegalStateException("Task title is required");
        if (id == null) id = new UUID_IRI().toURI();
        if (tags == null) tags = List.of();
        if (attributes == null) attributes = Map.of();

        if (created == null) {
            created = Instant.now();
        } else {
            modified = Instant.now();
        }
        if (isCompleted != null) {
            if (isCompleted) {
                completed = Instant.now();
            } else {
                completed = null;
            }
        }
        return new ToDo(
                id,
                title,
                description,
                tags,
                attributes,
                created,
                modified,
                completed,
                isCompleted,
                priority,
                assignee,
                parent);
    }
}
