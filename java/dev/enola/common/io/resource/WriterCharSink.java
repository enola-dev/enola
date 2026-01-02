/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource;

import com.google.common.io.CharSink;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A {@link CharSink} which delegates to a {@link Writer}. The underlying Writer is intentionally
 * never closed (by this CharSink).
 */
// Intentionally package local (for now)
class WriterCharSink extends CharSink {
    private final Writer writer;

    public WriterCharSink(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Writer openStream() throws IOException {
        return new NonClosingWriter(writer);
    }

    private static final class NonClosingWriter extends FilterWriter {

        private NonClosingWriter(Writer delegate) {
            super(delegate);
        }

        @Override
        public void close() throws IOException {
            // Do not close! But do flush in case the writer has a buffer that needs to be sent to
            // an underlying OutputStream.
            flush();
        }
    }
}
