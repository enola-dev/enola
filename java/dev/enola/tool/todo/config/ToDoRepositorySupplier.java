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
package dev.enola.tool.todo.config;

import dev.enola.common.io.resource.FileResource;
import dev.enola.tool.todo.ToDoRepository;
import dev.enola.tool.todo.file.ToDoRepositoryFile;

import java.io.IOException;
import java.util.function.Supplier;

public final class ToDoRepositorySupplier implements Supplier<ToDoRepository> {

    @Override
    public ToDoRepository get() {
        var homeDir = System.getProperty("user.home");
        var toDoFile = new java.io.File(homeDir, "ToDo.yaml").toURI();
        var toDoResource = new FileResource(toDoFile);
        try {
            return new ToDoRepositoryFile(toDoResource);
        } catch (IOException e) {
            throw new IllegalArgumentException(toDoResource.toString(), e);
        }
    }
}
