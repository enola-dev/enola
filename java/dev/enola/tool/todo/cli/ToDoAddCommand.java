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
package dev.enola.tool.todo.cli;

import dev.enola.tool.todo.ToDo;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "add", description = "Add a ToDo item")
public class ToDoAddCommand implements Callable<Integer> {

    @ParentCommand ToDoMain parent;

    @Parameters(index = "0", description = "Title of the ToDo item")
    String title;

    @Override
    public Integer call() throws IOException {
        var todo = new ToDo();
        todo.title = title;
        parent.repository.store(todo);
        return 0;
    }
}
