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
package dev.enola.common.exec.pty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

// TODO Write a SEPARATE (!) thingie for ANSI filtering...
// TODO If kept, then later move this to dev.enola.common.io
class InputStreamToAppendablePumper {
    // private static final Logger LOG =
    // LoggerFactory.getLogger(InputStreamToAppendablePumper.class);

    // TODO Thread management like StreamPumper, but shared via class Worker!

    public static final int BUFFER_SIZE = 4096;

    private final InputStream inputStream;
    private final Appendable appendable;
    private final Charset charset;

    InputStreamToAppendablePumper(
            InputStream inputStream,
            Charset charset,
            Appendable appendable,
            boolean immediateAppend)
            throws IOException {
        this.inputStream = inputStream;
        this.charset = charset;
        this.appendable = appendable;

        if (immediateAppend) pumpByCharacterBuffer();
        else pumpLineByLine();
    }

    private void pumpLineByLine() throws IOException {
        try (var isr = new InputStreamReader(inputStream, charset);
                var reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                appendable.append(line).append(System.lineSeparator());
            }
        }
    }

    private void pumpByCharacterBuffer() throws IOException {
        try (var isr = new InputStreamReader(inputStream, charset)) {
            char[] buffer = new char[BUFFER_SIZE];
            int charsRead;
            while ((charsRead = isr.read(buffer)) != -1) {
                appendable.append(CharBuffer.wrap(buffer, 0, charsRead));
            }
        }
    }
}
