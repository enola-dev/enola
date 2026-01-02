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
package dev.enola.common.string2long;

import com.google.common.io.CharSink;
import com.google.common.io.CharSource;

import java.io.IOException;

/**
 * Writes out {@link StringToLongBiMap} and reads them back in.
 *
 * <p>The Long ID is simply the line number of the symbol in the file.
 */
public final class StringToLongBiMapIO {

    // TODO Write JSON equivalent!

    public static void read(CharSource charSource, StringToLongBiMap.Builder into)
            throws IOException {
        charSource.forEachLine(into::put);
    }

    public static void write(StringToLongBiMap map, CharSink charSink) throws IOException {
        // Nota bene: This could not be read() back if a symbol contained CR or LF itself!
        charSink.writeLines(map.symbols(), "\n");
    }

    private StringToLongBiMapIO() {}
}
