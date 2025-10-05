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

import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.todo.ToDo;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class ToDoRepositoryFileTest {

    @Test
    public void testSaveAndFind() throws IOException {
        var resource = new MemoryResource(YamlMediaType.YAML_UTF_8);
        var repo1 = new ToDoRepositoryFile(resource);

        var todo1 = new ToDo();
        todo1.id = URI.create("urn:todo:1");
        todo1.title = "Test ToDo 1";
        repo1.save(todo1);

        var repo2 = new ToDoRepositoryFile(resource);
        var fetched = repo2.findById(todo1.id);
        assert fetched != null;
        assert fetched.id.equals(todo1.id);
        assert fetched.title.equals(todo1.title);
    }
}
