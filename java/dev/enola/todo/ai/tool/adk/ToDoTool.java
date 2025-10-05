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
package dev.enola.todo.ai.tool.adk;

import com.google.adk.tools.Annotations;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;
import com.google.common.collect.ImmutableMap;

import dev.enola.ai.adk.tool.Tools;
import dev.enola.common.SuccessOrError;
import dev.enola.todo.ToDo;
import dev.enola.todo.ToDoRepository;

import java.util.Map;

public class ToDoTool {

    private final ToDoRepository toDoRepository;

    public ToDoTool(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    public Map<String, BaseTool> createToolSet() {
        return ImmutableMap.of(
                "list_todo", FunctionTool.create(this, "listToDos")
                // "write_file", FunctionTool.create(this, "writeFile")
                );
    }

    @Annotations.Schema(description = "List all of my ToDo Task items")
    public Map<String, Iterable<ToDo>> listToDos() {
        return Tools.toMap(SuccessOrError.success(toDoRepository.list()));
    }
}
