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
package dev.enola.common.linereader.jline;

import static org.jline.reader.LineReader.Option.DISABLE_EVENT_EXPANSION;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.FreedesktopDirectories;
import dev.enola.common.linereader.ExecutionContext;
import dev.enola.common.linereader.IO;

import org.jline.console.CmdDesc;
import org.jline.console.CmdLine;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.LineReader.Option;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.widget.AutopairWidgets;
import org.jline.widget.AutosuggestionWidgets;
import org.jline.widget.TailTipWidgets;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Function;

/** JLineIO is an {@link IO} implementation based on <a href="https://jline.org">JLine.org</a>. */
public class JLineIO implements IO, Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(JLineIO.class);

    // TODO Enable "print above" for for progressive LLM response completion
    //   see https://jline.org/docs/advanced/interactive-features
    //   and update https://github.com/enola-dev/enola/issues/1377

    // TODO Enable configuring syntax highlighting; e.g. for @ and / and filenames
    //   see https://jline.org/docs/advanced/syntax-highlighting

    // TODO TailTipWidgets "qqqqqqqqqqq" https://github.com/jline/jline3/issues/1259

    // TODO End-user configurable keybindings; see https://github.com/jline/jline3/issues/398

    // TODO Enable Undo keybinding

    private final Terminal terminal;
    private final LineReader lineReader;
    private final ImmutableMap<String, String> env;
    private final ExecutionContext ctx;

    public JLineIO() throws IOException {
        this(
                System.getenv(),
                TerminalBuilder.terminal(),
                new DefaultParser(),
                NullCompleter.INSTANCE,
                ImmutableMap.of(),
                null,
                true);
    }

    /**
     * Constructor.
     *
     * @param terminal the Terminal
     * @param parser the Parser
     * @param completer the Completer
     * @param disableEventExpansion whether to disable special handling of magic history expansion
     *     commands like "!" and "!!" and "!n" and "!-n" and "!string" and "^string1^string2".
     */
    public JLineIO(
            Map<String, String> env,
            Terminal terminal,
            Parser parser,
            Completer completer,
            ImmutableMap<String, CmdDesc> tailTips,
            @Nullable Function<CmdLine, CmdDesc> descFun,
            boolean disableEventExpansion) {
        this.env = ImmutableMap.copyOf(env);
        this.terminal = terminal;

        // Jline's TailTipWidgets, used below, overwrite what's already on the screen.
        // So to avoid it from looking ugly, we need to clear the screen before using it.
        // In order to make the UX nicer, we save the current content of the screen (smcup),
        // and in the close() method restore it again (rmcup).
        terminal.puts(InfoCmp.Capability.enter_ca_mode);
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();

        if (terminal.trackMouse(Terminal.MouseTracking.Normal))
            LOG.info("Terminal has mouse tracking support");
        else LOG.info("Terminal does not have mouse tracking support");
        terminal.flush();

        this.lineReader =
                LineReaderBuilder.builder()
                        .parser(parser)
                        .terminal(terminal)
                        .variable(LineReader.MOUSE, true)
                        //
                        .completer(completer)
                        .option(Option.USE_FORWARD_SLASH, true) // use / as the directory separator
                        .option(Option.AUTO_LIST, true) // Automatically list options
                        .option(Option.LIST_PACKED, true) // Display compact completions
                        .option(Option.AUTO_MENU, true) // Show menu automatically
                        .option(Option.MENU_COMPLETE, true) // Cycle through completions

                        // See https://github.com/jline/jline3/issues/1218
                        .option(DISABLE_EVENT_EXPANSION, disableEventExpansion)
                        .history(new DefaultHistory())
                        .variable(LineReader.HISTORY_FILE, FreedesktopDirectories.HISTORY)
                        .option(Option.HISTORY_BEEP, false)
                        // ? .variable(LineReader.EXPAND_HISTORY, Boolean.TRUE)

                        // https://github.com/jline/jline3/wiki/Auto-Indentation-and-Pairing#auto-indentation ?

                        .build();

        new AutopairWidgets(lineReader, true).enable();

        new AutosuggestionWidgets(lineReader).enable();

        if (!tailTips.isEmpty())
            new TailTipWidgets(lineReader, tailTips, 5, TailTipWidgets.TipType.COMBINED).enable();

        if (descFun != null)
            new TailTipWidgets(lineReader, descFun, 5, TailTipWidgets.TipType.COMBINED).enable();

        KeyMap<Binding> keyMap = lineReader.getKeyMaps().get(LineReader.MAIN);
        keyMap.bind(new Reference(AutopairWidgets.TAILTIP_TOGGLE), KeyMap.alt("s"));

        InputRC.apply(lineReader);

        this.ctx =
                new ExecutionContext() {
                    @Override
                    public ImmutableMap<String, String> environment() {
                        return JLineIO.this.env;
                    }

                    @Override
                    public InputStream input() {
                        return terminal.input();
                    }

                    @Override
                    public OutputStream output() {
                        return terminal.output();
                    }

                    @Override
                    public OutputStream error() {
                        // TODO https://github.com/jline/jline3/issues/1318
                        return terminal.output();
                    }

                    @Override
                    public Charset inputCharset() {
                        return terminal.stdinEncoding();
                    }

                    @Override
                    public Charset outputCharset() {
                        return terminal.stdoutEncoding();
                    }

                    @Override
                    public Charset errorCharset() {
                        // TODO https://github.com/jline/jline3/issues/1318
                        return terminal.stderrEncoding();
                    }
                };
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
    public ExecutionContext ctx() {
        return ctx;
    }

    @Override
    public void close() throws IOException {
        // Restore the previous screen content
        terminal.puts(InfoCmp.Capability.exit_ca_mode);

        terminal.trackMouse(Terminal.MouseTracking.Off);
        terminal.flush();

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
