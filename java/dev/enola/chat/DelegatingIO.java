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

/**
 * DelegatingIO is an {@link dev.enola.chat.IO} implementation which if STDIN and/or STDOUT have
 * been redirected reads from there (and "echos" input), and otherwise delegates to another IO
 * implementation (typically e.g., a {@link dev.enola.chat.ConsoleIO} or a {@link
 * dev.enola.chat.jline.JLineIO}).
 */
public class DelegatingIO implements IO {

    private final IO delegate;

    public DelegatingIO(IO delegate) {
        if (System.console() == null) this.delegate = new SystemInOutIO();
        else this.delegate = delegate;
    }

    @Override
    public @Nullable String readLine() {
        return delegate.readLine();
    }

    @Override
    public @Nullable String readLine(String prompt) {
        return delegate.readLine(prompt);
    }

    @Override
    public @Nullable String readLine(String promptFormat, Object... args) {
        return delegate.readLine(promptFormat, args);
    }

    @Override
    public void printf(String format, Object... args) {
        delegate.printf(format, args);
    }
}
