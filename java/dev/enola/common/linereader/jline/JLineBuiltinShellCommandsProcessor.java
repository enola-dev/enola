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
package dev.enola.common.linereader.jline;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.FreedesktopDirectories;
import dev.enola.common.function.CheckedConsumer;

import org.jline.builtins.ConfigurationPath;
import org.jline.console.CmdDesc;
import org.jline.console.CommandRegistry;
import org.jline.console.impl.Builtins;
import org.jline.reader.LineReader;
import org.jline.reader.Widget;
import org.jline.reader.impl.completer.SystemCompleter;
import org.jline.terminal.Terminal;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class JLineBuiltinShellCommandsProcessor implements CheckedConsumer<String, Exception> {
    // TODO Rename JLineBuiltinShellCommandsProcessor to JLineBuiltinCommandsProcessor

    // TODO https://github.com/jline/jline3/issues/1256

    // TODO https://github.com/jline/jline3/issues/1260 history IllegalArgumentException

    private final CommandRegistry.CommandSession commandSession;
    private final Builtins builtins;
    private final SystemCompleter completer;
    private final ImmutableMap<String, CmdDesc> commandDescriptions;
    private final Supplier<Path> cwdSupplier;
    private @Nullable LineReader lineReader;

    public JLineBuiltinShellCommandsProcessor(Terminal terminal) {
        commandSession = new Builtins.CommandSession(terminal);
        // TODO Integrate with cwd in ExecAgent - but keep Optional!
        this.cwdSupplier = () -> null; // Path.of("/");
        Function<String, Widget> widgetCreator = name -> null;
        var configDir = FreedesktopDirectories.JLINE_CONFIG_DIR;
        var configurationPath = new ConfigurationPath(configDir, configDir);

        // TODO Limit exposed Builtins commands?
        builtins = new Builtins(cwdSupplier, configurationPath, widgetCreator);
        // builtins.alias("bindkey", "keymap");

        completer = builtins.compileCompleters();
        completer.compile();

        this.commandDescriptions = CmdDescs.buildMap(builtins);
    }

    public void lineReader(LineReader lineReader) {
        this.lineReader = lineReader;
        builtins.setLineReader(lineReader);
    }

    @Override
    public void accept(String commandLine) throws Exception {
        if (lineReader == null) throw new IllegalStateException("lineReader not set!");

        // split() won't handle quoted arguments correctly, which is fine here (for simple
        // Builtins), but don't re-use this as-is for other more complex external commands.
        var splitCommandLine = List.of(commandLine.split("\\s+"));
        var command = splitCommandLine.getFirst();
        if (!builtins.hasCommand(command)) return;
        if (splitCommandLine.size() > 1) {
            var rest = splitCommandLine.subList(1, splitCommandLine.size());
            builtins.invoke(commandSession, command, rest);
        } else builtins.invoke(commandSession, command);
    }

    public Supplier<Path> cwdSupplier() {
        return cwdSupplier;
    }

    public SystemCompleter completer() {
        return completer;
    }

    public CommandRegistry commandRegistry() {
        return builtins;
    }

    public ImmutableMap<String, CmdDesc> commandDescriptions() {
        return commandDescriptions;
    }
}
