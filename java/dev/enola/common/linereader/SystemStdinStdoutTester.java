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
package dev.enola.common.linereader;

import java.io.*;
import java.nio.charset.Charset;

public class SystemStdinStdoutTester {

    // TODO captureErr()

    // TODO combinations of System.in, System.out, System.err

    // TODO Rethink Charset... should be a constructor argument?!

    public static void pipeIn(String input, Runnable runnable) {
        InputStream original = System.in;
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(consoleCharset())));
            runnable.run();
        } finally {
            System.setIn(original);
        }
    }

    public static String captureOut(Runnable runnable) {
        var original = System.out;
        try {
            var out = new ByteArrayOutputStream();
            var ps = new PrintStream(out, true, consoleCharset());
            System.setOut(ps);
            runnable.run();
            return out.toString(consoleCharset());
        } finally {
            System.setOut(original);
        }
    }

    private static Charset consoleCharset() {
        if (System.console() != null) return System.console().charset();
        return Charset.defaultCharset();
    }
}
