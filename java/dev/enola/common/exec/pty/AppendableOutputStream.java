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

import dev.enola.common.io.util.AppendableWriter;

import org.jline.utils.WriterOutputStream;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * An OutputStream that writes to an Appendable. This class bridges the gap between byte-oriented
 * output streams and character-oriented appendable interfaces, allowing byte data to be written to
 * a character-based destination like a StringBuilder, Writer, or CharBuffer.
 */
class AppendableOutputStream extends WriterOutputStream {
    // TODO If kept and ever needed elsewhere, then later move this to dev.enola.common.io.util

    // NOTE: We originally had our own full implementation of this, but it was buggy. Then we
    // discovered that JLine already had something very close, and now we just extend that one.

    private final Appendable appendable;

    /**
     * Constructs an AppendableOutputStream with the specified Appendable and charset.
     *
     * @param appendable The Appendable to which bytes will be written as characters.
     * @param charset The Charset to use for decoding bytes into characters.
     */
    public AppendableOutputStream(Appendable appendable, Charset charset) {
        super(new AppendableWriter(appendable), charset);
        this.appendable = appendable;
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        if (appendable instanceof java.io.Flushable flushable) flushable.flush();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (appendable instanceof java.io.Closeable closeable) closeable.close();
    }
}
