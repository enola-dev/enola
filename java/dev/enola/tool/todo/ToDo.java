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

import dev.enola.data.id.UUID_IRI;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ToDo {
    // TODO implements Identifiable<URI>

    // TODO Generate record and Builder, from a model; but for now, this is good enough

    public URI id;
    public String title;
    public String description;

    public final List<String> tags = new java.util.ArrayList<>();
    public final Map<String, String> attributes = new java.util.HashMap<>();

    public Instant created;
    public Instant modified;
    public Instant completed;
    public Boolean isCompleted;

    // status is intentionally omitted, because different backends will have differences.
    public Byte priority;

    /** Assignee URI; e.g. <code>mailto:user@example.org</code>. */
    public URI assignee;

    /** Parent ToDo ID; e.g. a "Project". */
    // TODO Add other more flexible relationships, e.g. "depends on", "related to" etc.
    // TODO public Set<URI> children = new HashSet<>();
    public URI parent;

    // TODO Discussion, like in a bug tracker, with comments by users etc.

    // TODO public Set<URI> attachments = new HashSet<>();

    public void prepareForSave() {
        if (title == null) throw new IllegalStateException("Task title is required: " + this);
        if (id == null) id = new UUID_IRI().toURI();

        if (created == null) created = Instant.now();
        else modified = Instant.now();
        if (isCompleted != null) {
            if (isCompleted) {
                completed = Instant.now();
            } else {
                completed = null;
            }
        }
    }
}
