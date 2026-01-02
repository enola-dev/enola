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

import com.google.common.io.ByteSink;
import com.google.common.io.CharSink;
import com.google.common.net.MediaType;

import java.io.Writer;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link WritableResource} which delegates to a {@link Writer}. The underlying Writer is
 * intentionally never closed by this Resource's CharSink.
 */
public class WriterResource extends BaseResource implements WritableResource {

    private static AtomicLong counter = new AtomicLong();

    private final Writer writer;

    public WriterResource(Writer writer, MediaType mediaType) {
        super(URI.create("writer:" + Long.toHexString(counter.incrementAndGet())), mediaType);
        this.writer = writer;
    }

    @Override
    public CharSink charSink() {
        return new WriterCharSink(writer);
    }

    @Override
    public ByteSink byteSink() {
        // To implement, use class WriterOutputStream.java from
        // https://github.com/apache/commons-io/blob/master/src/main/java/org/apache/commons/io/output/
        throw new UnsupportedOperationException(
                "TODO Implement byteSink(), or use e.g. --output file:something.ext");
    }
}
