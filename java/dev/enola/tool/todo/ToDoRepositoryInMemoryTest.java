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

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;

public class ToDoRepositoryInMemoryTest {

    @Test
    public void basics() {
        var repo = new ToDoRepositoryInMemory();

        var todo1 =
                ToDo.builder()
                        .id(URI.create("urn:todo:1"))
                        .title("Test ToDo 1")
                        .description("This is a test ToDo item.")
                        .tags(ImmutableList.of("test", "todo"))
                        .attributes(ImmutableMap.of("key1", "value1", "key2", "value2"))
                        .build();
        repo.store(todo1);

        var fetched = repo.get(todo1.id());
        assertThat(fetched).isNotNull();
        assertThat(fetched.id()).isEqualTo(todo1.id());
        assertThat(fetched.title()).isEqualTo(todo1.title());
        assertThat(fetched.description().get()).isEqualTo(todo1.description().get());
        assertThat(fetched.tags()).hasSize(2);
        assertThat(fetched.attributes()).hasSize(2);

        var all = repo.list();
        var list = new ArrayList<ToDo>();
        all.forEach(list::add);
        assertThat(list).hasSize(1);

        repo.delete(todo1.id());
        assertThrows(IllegalArgumentException.class, () -> repo.get(URI.create("urn:todo:1")));
    }

    @Test
    public void timestamps() {
        var repo = new ToDoRepositoryInMemory();
        var todo = ToDo.builder().title("Test").build();

        // 1. Initial Store (NEW)
        repo.store(todo);
        var stored1 = repo.get(todo.id());
        assertThat(stored1.created().isPresent()).isTrue();
        assertThat(stored1.completed().isPresent()).isFalse();

        // 2. Mark Completed
        var stored2 = stored1.toBuilder().completed(Instant.now()).build();
        repo.store(stored2);
        var stored3 = repo.get(todo.id());
        assertThat(stored3.isCompleted()).isTrue();
        assertThat(stored3.completed().isPresent()).isTrue();

        // 3. Update while completed
        var completedAt = stored3.completed();
        var stored4 = stored3.toBuilder().description("updated").build();
        repo.store(stored4);
        var stored5 = repo.get(todo.id());
        assertThat(stored5.completed()).isEqualTo(completedAt);
    }
}
