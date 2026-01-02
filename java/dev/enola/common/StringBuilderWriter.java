/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common;

import java.io.IOException;
import java.io.Writer;

/**
 * {@link Writer} implementation that outputs to a {@link StringBuilder}.
 *
 * <p>This is alternative to {@link java.io.StringWriter}, which internally uses a {@link
 * StringBuffer}. This is faster (!) - at the expense of not (!) being concurrency multi thread safe
 * - which often is not required.
 */
public final class StringBuilderWriter extends Writer {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void write(String str) throws IOException {
        builder.append(str);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        builder.append(cbuf);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len == 0) return;
        builder.append(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if (len == 0) return;
        builder.append(str, off, off + len); // not off, len!
    }

    @Override
    public void write(int c) throws IOException {
        builder.append((char) c); // sic!
    }

    @Override
    public Writer append(char c) throws IOException {
        builder.append(c);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {}
}
