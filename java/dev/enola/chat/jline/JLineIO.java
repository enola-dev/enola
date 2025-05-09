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
package dev.enola.chat.jline;

import static org.jline.reader.LineReader.Option.DISABLE_EVENT_EXPANSION;

import dev.enola.chat.IO;
import dev.enola.common.FreedesktopDirectories;

import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jspecify.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

/** JLineIO is an {@link IO} implementation based on <a href="https://jline.org">JLine.org</a>. */
public class JLineIO implements IO, Closeable {

    // TODO Make current hard-coded key bindings end-user configurable

    // TODO Keybindings; see https://github.com/jline/jline3/issues/398

    // TODO https://github.com/jline/jline3/wiki/History
    // TODO Regularly do lineReader.getHistory().save(); // NOT .write(path, true);

    private final Terminal terminal;
    private final LineReader lineReader;

    public JLineIO() throws IOException {
        this(TerminalBuilder.terminal(), true);
    }

    /**
     * Constructor.
     *
     * @param terminal the Terminal
     * @param disableEventExpansion whether or not to disable special handling of magic history
     *     expansion commands like "!" and "!!" and "!n" and "!-n" and "!string" and
     *     "^string1^string2".
     * @throws IOException
     */
    public JLineIO(Terminal terminal, boolean disableEventExpansion) throws IOException {
        this.terminal = terminal;

        this.lineReader =
                LineReaderBuilder.builder()
                        .terminal(terminal)
                        // See https://github.com/jline/jline3/issues/1218
                        .option(DISABLE_EVENT_EXPANSION, disableEventExpansion)
                        // TODO Test/Doc! .option(LineReader.Option.MOUSE, true)

                        .history(new DefaultHistory())
                        .variable(LineReader.HISTORY_FILE, FreedesktopDirectories.HISTORY)
                        .option(LineReader.Option.HISTORY_BEEP, false)
                        // ? .variable(LineReader.EXPAND_HISTORY, Boolean.TRUE)

                        .build();

        KeyMap<Binding> map = lineReader.getKeyMaps().get(LineReader.MAIN);
        map.bind(new Reference(LineReader.BACKWARD_KILL_WORD), KeyMap.ctrl('\u0008'));
    }

    @Override
    public @Nullable String readLine() {
        try {
            return lineReader.readLine();
        } catch (EndOfFileException | UserInterruptException e) {
            return null;
        }
    }

    @Override
    public @Nullable String readLine(String prompt) {
        try {
            return lineReader.readLine(prompt);
        } catch (EndOfFileException | UserInterruptException e) {
            return null;
        }
    }

    @Override
    public void printf(String format, Object... args) {
        terminal.writer().printf(format, args);
    }

    @Override
    public void close() throws IOException {
        terminal.close();

        // NB: JLine already saves History in a background thread; this is just to save the history
        // file in case of a close() call before that background thread had another chance to save
        // it.
        lineReader.getHistory().save();
    }
}
