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

import com.google.common.collect.ImmutableMap;

import dev.enola.chat.IO;
import dev.enola.common.FreedesktopDirectories;

import org.jline.console.CmdDesc;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.AutopairWidgets;
import org.jline.widget.AutosuggestionWidgets;
import org.jline.widget.TailTipWidgets;
import org.jspecify.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

/** JLineIO is an {@link IO} implementation based on <a href="https://jline.org">JLine.org</a>. */
public class JLineIO implements IO, Closeable {

    // TODO Enable "print above" for for progressive LLM response completion
    //   see https://jline.org/docs/advanced/interactive-features
    //   and update https://github.com/enola-dev/enola/issues/1377

    // TODO Enable configuring syntax highlighting; e.g. for @ and / and filenames
    //   see https://jline.org/docs/advanced/syntax-highlighting

    // TODO TailTipWidgets "qqqqqqqqqqq" https://github.com/jline/jline3/issues/1259

    // TODO End-user configurable keybindings; see https://github.com/jline/jline3/issues/398

    // TODO Mouse support; see https://github.com/jline/jline3/issues/1254

    // TODO Enable Undo keybinding

    private final Terminal terminal;
    private final LineReader lineReader;

    public JLineIO() throws IOException {
        this(TerminalBuilder.terminal(), NullCompleter.INSTANCE, ImmutableMap.of(), true);
    }

    /**
     * Constructor.
     *
     * @param terminal the Terminal
     * @param completer the Completer
     * @param disableEventExpansion whether to disable special handling of magic history expansion
     *     commands like "!" and "!!" and "!n" and "!-n" and "!string" and "^string1^string2".
     */
    public JLineIO(
            Terminal terminal,
            Completer completer,
            ImmutableMap<String, CmdDesc> tailTips,
            boolean disableEventExpansion) {
        this.terminal = terminal;

        this.lineReader =
                LineReaderBuilder.builder()
                        .parser(new DefaultParser())
                        .terminal(terminal)
                        //
                        .completer(completer)
                        .option(LineReader.Option.AUTO_LIST, true) // Automatically list options
                        .option(LineReader.Option.LIST_PACKED, true) // Display compact completions
                        .option(LineReader.Option.AUTO_MENU, true) // Show menu automatically
                        .option(LineReader.Option.MENU_COMPLETE, true) // Cycle through completions

                        // See https://github.com/jline/jline3/issues/1218
                        .option(DISABLE_EVENT_EXPANSION, disableEventExpansion)
                        // TODO Test/Doc! .option(LineReader.Option.MOUSE, true)

                        .history(new DefaultHistory())
                        .variable(LineReader.HISTORY_FILE, FreedesktopDirectories.HISTORY)
                        .option(LineReader.Option.HISTORY_BEEP, false)
                        // ? .variable(LineReader.EXPAND_HISTORY, Boolean.TRUE)

                        // https://github.com/jline/jline3/wiki/Auto-Indentation-and-Pairing#auto-indentation ?

                        .build();

        new AutopairWidgets(lineReader, true).enable();

        new AutosuggestionWidgets(lineReader).enable();

        if (!tailTips.isEmpty())
            new TailTipWidgets(lineReader, tailTips, 5, TailTipWidgets.TipType.COMBINED).enable();

        // KeyMap<Binding> map = lineReader.getKeyMaps().get(LineReader.MAIN);
        // map.bind(new Reference(LineReader.BACKWARD_KILL_WORD), KeyMap.ctrl('\u0008'));

        InputRC.apply(lineReader);
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

    public Terminal terminal() {
        return terminal;
    }

    public LineReader lineReader() {
        return lineReader;
    }
}
