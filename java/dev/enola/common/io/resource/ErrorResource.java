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
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ErrorResource extends BaseResource implements Resource {

    public static class Provider implements ResourceProvider {

        @Override
        public Resource getResource(URI uri) {
            if (SCHEME.equals(uri.getScheme())) return ErrorResource.INSTANCE;
            else return null;
        }
    }

    static final String SCHEME = "error";

    private static final URI ERROR_URI = URI.create("error:-");

    private static final MediaType MEDIA_TYPE = MediaType.OCTET_STREAM;

    // Must be *AFTER* above! static field initialization in Java is dumb...
    public static final ErrorResource INSTANCE = new ErrorResource();

    private ErrorResource() {
        super(ERROR_URI, MEDIA_TYPE);
    }

    @Override
    public ByteSink byteSink() {
        return ErrorByteSink.INSTANCE;
    }

    @Override
    public ByteSource byteSource() {
        return new ErrorByteSource(new IOException());
    }

    private static final class ErrorByteSink extends ByteSink {
        public static final ByteSink INSTANCE = new ErrorByteSink();

        private ErrorByteSink() {}

        @Override
        public OutputStream openStream() throws IOException {
            return new ErrorOutputStream();
        }
    }

    private static final class ErrorOutputStream extends OutputStream {
        @Override
        public void write(int i) throws IOException {
            throw new IOException();
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            throw new IOException();
        }
    }
}
