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

import org.jspecify.annotations.Nullable;

import java.io.Console;

/** ConsoleIO is an {@link IO} implementation based on {@link Console}. */
public class ConsoleIO implements IO {
    private final Console console;

    public ConsoleIO() {
        if (System.console() == null)
            throw new IllegalStateException("Use another implementation of interface IO");
        console = System.console();
    }

    @Override
    public @Nullable String readLine() {
        return console.readLine();
    }

    @Override
    public @Nullable String readLine(String prompt) {
        printf(prompt);
        return readLine();
    }

    @Override
    public void printf(String format, Object... args) {
        console.printf(format, args);
    }
}
