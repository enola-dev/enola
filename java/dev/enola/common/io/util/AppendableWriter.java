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
package dev.enola.common.io.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

// See also org.apache.commons.io.output.AppendableWriter; but
//   we (a) want to avoid the dependency; and (b) handle flush() and close().
public class AppendableWriter extends java.io.Writer {
    private final Appendable appendable;

    public AppendableWriter(Appendable appendable) {
        this.appendable = appendable;
    }

    @Override
    public Writer append(char c) throws IOException {
        appendable.append(c);
        return this;
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        appendable.append(csq);
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        appendable.append(csq, start, end);
        return this;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        appendable.append(str, off, off + len); // !!
    }

    @Override
    public void write(String str) throws IOException {
        appendable.append(str);
    }

    @Override
    public void write(int c) throws IOException {
        appendable.append((char) c);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        appendable.append(CharBuffer.wrap(cbuf));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        // This cannot work if it doesn't happen to be at the right boundary?
        // But we cannot really do anything better here... or can we?
        // org.apache.commons.io.output.AppendableWriter also does it.
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
