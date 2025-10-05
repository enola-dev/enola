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

import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;

public class ToDoRepositoryMemoryTest {

    @Test
    public void basics() {
        var repo = new ToDoRepositoryMemory();

        var todo1 = new ToDo();
        todo1.id = URI.create("urn:todo:1");
        todo1.title = "Test ToDo 1";
        todo1.description = "This is a test ToDo item.";
        todo1.tags.add("test");
        todo1.tags.add("todo");
        todo1.attributes.put("key1", "value1");
        todo1.attributes.put("key2", "value2");

        repo.save(todo1);

        var fetched = repo.findById(todo1.id);
        assert fetched != null;
        assert fetched.id.equals(todo1.id);
        assert fetched.title.equals(todo1.title);
        assert fetched.description.equals(todo1.description);
        assert fetched.tags.size() == 2;
        assert fetched.attributes.size() == 2;

        var all = repo.findAll();
        var list = new ArrayList<ToDo>();
        all.forEach(list::add);
        assert list.size() == 1;

        repo.delete(URI.create("urn:todo:1"));
        try {
            repo.findById(URI.create("urn:todo:1"));
            assert false; // Should not reach here
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
}
