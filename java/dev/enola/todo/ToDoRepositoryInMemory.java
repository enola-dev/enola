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
package dev.enola.todo;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToDoRepositoryInMemory implements ToDoRepository {

    private final Map<URI, ToDo> store = new ConcurrentHashMap<>();

    @Override
    public ToDo get(URI id) {
        var toDo = store.get(id);
        if (toDo == null) {
            throw new IllegalArgumentException("ToDo item not found: " + id);
        }
        return toDo;
    }

    @Override
    public Iterable<ToDo> list() {
        return store.values();
    }

    @Override
    public void store(ToDo todo) {
        todo.prepareForSave();
        store.put(todo.id, todo);
    }

    @Override
    public void delete(URI id) {
        store.remove(id);
    }
}
