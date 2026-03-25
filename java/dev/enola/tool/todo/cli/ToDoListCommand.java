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

import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.object.jackson.JacksonObjectReaderWriterChain;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "list", description = "List ToDo items")
public class ToDoListCommand implements Callable<Integer> {

    @ParentCommand ToDoMain parent;

    @Spec CommandSpec spec;

    private final JacksonObjectReaderWriterChain writer = new JacksonObjectReaderWriterChain();

    @Override
    public Integer call() throws IOException {
        var hadFailure = false;
        var yaml = YamlMediaType.YAML_UTF_8;
        for (var todo : parent.repository.list()) {
            var opt = writer.write(todo, yaml);
            if (opt.isPresent()) {
                spec.commandLine().getOut().println(opt.get());
            } else {
                spec.commandLine()
                        .getErr()
                        .println("warning: failed to serialize ToDo item: " + todo);
                hadFailure = true;
            }
        }
        return hadFailure ? 1 : 0;
    }
}
