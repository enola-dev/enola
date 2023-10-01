/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static java.util.Objects.requireNonNull;

import com.google.common.io.ByteSink;
import com.google.common.io.CharSink;
import com.google.common.net.MediaType;

import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * A {@link WritableResource} which delegates to a {@link Writer}. The underlying Writer is
 * intentionally never closed by this Resource's CharSink.
 */
public class WriterResource implements WritableResource {
    private final Writer writer;
    private final URI uri;
    private final MediaType mediaType;

    public WriterResource(Writer writer, MediaType mediaType) {
        this.writer = writer;
        this.mediaType = requireNonNull(mediaType, "mediaType");
        this.uri = URI.create("writer:" + Integer.toHexString(hashCode()));
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public MediaType mediaType() {
        return mediaType;
    }

    @Override
    public CharSink charSink() {
        return new WriterCharSink(writer);
    }

    @Override
    public CharSink charSink(Charset defaultCharset) {
        // Ignores defaultCharset argument
        return charSink();
    }

    @Override
    public ByteSink byteSink() {
        // To implement, use class WriterOutputStream.java from
        // https://github.com/apache/commons-io/blob/master/src/main/java/org/apache/commons/io/output/
        throw new UnsupportedOperationException(
                "TODO Implement byteSink(), or use e.g. --output file:something.ext");
    }
}
