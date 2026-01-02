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
package dev.enola.chat;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.chat.Prompter.MOTD;

import dev.enola.common.linereader.TestIO;
import dev.enola.common.secret.InMemorySecretManager;
import dev.enola.identity.Subjects;

import org.junit.Test;

import java.util.List;

public class DemoTest {

    // TODO This test behaves differently depending on whether or not Ollama is running, because
    // Prompter enables LangChain4jAgent if port 11434 is available... which is not so great, for
    // a reliable reproducible test! Rework Prompter to avoid this.

    @Test
    public void eof() {
        var io = new TestIO(List.of());
        new Prompter(new InMemorySecretManager()).chatLoop(io, new Subjects().alice(), false);
        assertThat(io.getOutput()).isEqualTo(MOTD + "Alice in #Lobby> ");
    }

    @Test
    public void hello() {
        var io = new TestIO(List.of("Hello"));
        new Prompter(new InMemorySecretManager()).chatLoop(io, new Subjects().alice(), false);
        assertThat(io.getOutput()).startsWith(MOTD + "Alice in #Lobby> ");
    }

    @Test
    public void echo() {
        var io = new TestIO(List.of("@echo yolo"));
        new Prompter(new InMemorySecretManager()).chatLoop(io, new Subjects().alice(), false);
        assertThat(io.getOutput()).startsWith(MOTD + "Alice in #Lobby> Echoer> yolo\n");
    }
}
