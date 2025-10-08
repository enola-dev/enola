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
package dev.enola.tool.todo;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;

public class ToDoRepositoryInMemoryTest {

    @Test
    public void basics() {
        var repo = new ToDoRepositoryInMemory();

        var todo1 = new ToDo();
        todo1.id = URI.create("urn:todo:1");
        todo1.title = "Test ToDo 1";
        todo1.description = "This is a test ToDo item.";
        todo1.tags.add("test");
        todo1.tags.add("todo");
        todo1.attributes.put("key1", "value1");
        todo1.attributes.put("key2", "value2");
        repo.store(todo1);

        var fetched = repo.get(todo1.id);
        assertThat(fetched).isNotNull();
        assertThat(fetched.id).isEqualTo(todo1.id);
        assertThat(fetched.title).isEqualTo(todo1.title);
        assertThat(fetched.description).isEqualTo(todo1.description);
        assertThat(fetched.tags).hasSize(2);
        assertThat(fetched.attributes).hasSize(2);

        var all = repo.list();
        var list = new ArrayList<ToDo>();
        all.forEach(list::add);
        assertThat(list).hasSize(1);

        repo.delete(todo1.id);
        assertThrows(IllegalArgumentException.class, () -> repo.get(URI.create("urn:todo:1")));
    }
}
