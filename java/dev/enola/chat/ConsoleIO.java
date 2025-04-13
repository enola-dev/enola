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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

class ConsoleIO implements IO {
    private static final Logger LOG = LoggerFactory.getLogger(ConsoleIO.class);

    private final @Nullable Console console = System.console();
    private final @Nullable BufferedReader reader;

    ConsoleIO() {
        if (console == null) {
            reader = new BufferedReader(new InputStreamReader(System.in, consoleCharset()));
        } else {
            reader = null; // Not needed when console is available
        }
    }

    @Override
    public @Nullable String readLine() {
        if (console != null) return console.readLine();
        try {
            var line = reader.readLine();
            if (line != null) System.out.println(line); // echo!
            return line;
        } catch (IOException e) {
            LOG.warn("readLine() from STDIN, without System.console(), failed", e);
            return null;
        }
    }

    @Override
    public void printf(String format, Object... args) {
        if (console != null) console.printf(format, args);
        else System.out.printf(format, args);
    }

    static Charset consoleCharset() {
        if (System.console() != null) return System.console().charset();
        return Charset.defaultCharset();
    }
}
