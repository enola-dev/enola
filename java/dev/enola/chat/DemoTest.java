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
package dev.enola.chat;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.identity.Subjects;

import org.junit.Test;

import java.util.List;

public class DemoTest {

    @Test
    public void eof() {
        var io = new TestIO(List.of());
        Demo.chat(io, new Subjects().alice());
        assertThat(io.getOutput()).containsExactly("Alice in #Lobby> ");
    }

    @Test
    public void helloAndQuit() {
        var io = new TestIO(List.of("Hello", "quit"));
        Demo.chat(io, new Subjects().alice());
        assertThat(io.getOutput())
                .containsExactly("Alice in #Lobby> ", "Alice in #Lobby> ")
                .inOrder();
    }

    @Test
    public void echo() {
        var io = new TestIO(List.of("@echo yolo"));
        Demo.chat(io, new Subjects().alice());
        assertThat(io.getOutput())
                .containsExactly("Alice in #Lobby> ", "Echoer> yolo\n", "Alice in #Lobby> ")
                .inOrder();
    }
}
