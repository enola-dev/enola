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
package dev.enola.common.exec.pty;

import java.io.IOException;
import java.nio.CharBuffer;

// TODO If kept, then later move this to dev.enola.common.io
class AppendableWriter extends java.io.Writer {
    private final Appendable appendable;

    public AppendableWriter(Appendable appendable) {
        this.appendable = appendable;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        // TODO This cannot work if it doesn't happen to be at the right boundary...
        appendable.append(CharBuffer.wrap(cbuf, off, len));
    }

    @Override
    public void flush() throws IOException {
        if (appendable instanceof java.io.Flushable flushable) flushable.flush();
    }

    @Override
    public void close() throws IOException {
        if (appendable instanceof java.io.Closeable closeable) closeable.close();
    }
}
