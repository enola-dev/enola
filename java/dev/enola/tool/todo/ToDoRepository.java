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

import java.io.IOException;
import java.net.URI;

/**
 * Repository of {@link ToDo} items.
 *
 * <p>This could be implemented for various backends, such as:
 *
 * <ul>
 *   <li>In-memory storage
 *   <li>File-based storage, like JSON or YAML files
 *   <li><a href="http://todotxt.org">Todo.txt</a> format
 *   <li>Scan for <code>TODO</code> comments in source code files
 *   <li>Integration with issue trackers (e.g., GitHub Issues, Jira, GitLab Issues)
 *   <li>Integration with task management systems (e.g., Google Tasks, Todoist, Microsoft ToDo)
 *   <li>Integration with calendar systems (e.g., Google Calendar, Microsoft Outlook)
 *   <li>Other remote API (REST, GraphQL, etc.)
 *   <li>Database storage (SQL or NoSQL)
 * </ul>
 */
public interface ToDoRepository {

    // TODO Implement dev.enola.data.Provider, Store etc. and use existing implementations

    ToDo get(URI id);

    Iterable<ToDo> list();

    void store(ToDo todo) throws IOException;

    void delete(URI id) throws IOException;
}
