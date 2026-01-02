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
package dev.enola.common.io.resource;

import com.google.common.net.MediaType;

import java.io.IOException;
import java.io.Writer;

public class AppendableResource extends WriterResource {

    public AppendableResource(Appendable appendable, MediaType mediaType) {
        super(writerToAppendable(appendable), mediaType);
    }

    private static Writer writerToAppendable(Appendable appendable) {
        return new Writer() {
            @Override
            public void write(char[] cbuf) throws IOException {
                for (var c : cbuf) appendable.append(c);
            }

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                for (int i = off; i < off + len; i++) appendable.append(cbuf[i]);
            }

            @Override
            public void write(String str) throws IOException {
                appendable.append(str);
            }

            @Override
            public void write(int character) throws IOException {
                appendable.append((char) character);
            }

            @Override
            public void write(String str, int off, int len) throws IOException {
                appendable.append(str.substring(off, off + len));
            }

            @Override
            public void flush() throws IOException {}

            @Override
            public void close() throws IOException {}
        };
    }
}
