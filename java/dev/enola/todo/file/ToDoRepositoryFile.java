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
package dev.enola.todo.file;

import dev.enola.common.collect.MoreIterables;
import dev.enola.common.io.object.ObjectReaderWriter;
import dev.enola.common.io.object.jackson.JacksonObjectReaderWriterChain;
import dev.enola.common.io.resource.Resource;
import dev.enola.todo.ToDo;
import dev.enola.todo.ToDoRepository;
import dev.enola.todo.ToDoRepositoryInMemory;

import java.io.IOException;
import java.net.URI;

public class ToDoRepositoryFile implements ToDoRepository {

    private final ToDoRepository delegate = new ToDoRepositoryInMemory();
    private final ObjectReaderWriter readerWriter;
    private final Resource resource;

    public ToDoRepositoryFile(Resource resource) throws IOException {
        this.readerWriter = new JacksonObjectReaderWriterChain();
        this.resource = resource;

        MoreIterables.forEach(readerWriter.readArray(resource, ToDo.class), delegate::store);
    }

    private void write() throws IOException {
        var tasks = delegate.list();
        if (!readerWriter.write(tasks, resource)) {
            throw new IOException("Failed to write ToDos to " + resource);
        }
    }

    @Override
    public ToDo get(URI id) {
        return delegate.get(id);
    }

    @Override
    public Iterable<ToDo> list() {
        return delegate.list();
    }

    @Override
    public void store(ToDo todo) throws IOException {
        delegate.store(todo);
        write();
    }

    @Override
    public void delete(URI id) throws IOException {
        delegate.delete(id);
        write();
    }
}
