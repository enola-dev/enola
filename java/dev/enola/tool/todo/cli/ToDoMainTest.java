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

import static com.google.common.truth.Truth.assertThat;

import dev.enola.cli.common.CLI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class ToDoMainTest {

    @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void addAndList() throws IOException {
        // Redirect user.home to a temporary directory for this test
        System.setProperty("user.home", tempFolder.getRoot().getAbsolutePath());

        // Add
        var addCli = new CLI(new String[] {"add", "Task 1"}, new ToDoMain());
        addCli.setOutAndErrStrings();
        assertThat(addCli.execute()).isEqualTo(0);
        assertThat(addCli.getErrString()).isEmpty();

        // List
        var listCli = new CLI(new String[] {"list"}, new ToDoMain());
        listCli.setOutAndErrStrings();
        assertThat(listCli.execute()).isEqualTo(0);
        assertThat(listCli.getErrString()).isEmpty();
        assertThat(listCli.getOutString()).contains("title: Task 1");
    }
}
