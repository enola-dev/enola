/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import java.io.*;
import java.net.URI;

/**
 * Resource for URIs "fd:0?charset=ASCII" (STDIN), "fd:1?charset=UTF-8" (STDOUT),
 * "fd:2?charset=UTF-16BE" STDERR.
 */
public class FileDescriptorResource implements Resource {

    private final FileDescriptor fileDescriptor;
    private final MediaType mediaType;
    private final URI uri;

    public FileDescriptorResource(URI uri) {
        if (!"fd".equals(uri.getScheme())) {
            throw new IllegalArgumentException(uri.toString());
        }

        var fd = uri.getSchemeSpecificPart();
        if (fd.startsWith("0")) {
            fileDescriptor = FileDescriptor.in;
        } else if (fd.startsWith("1")) {
            fileDescriptor = FileDescriptor.out;
        } else if (fd.startsWith("2")) {
            fileDescriptor = FileDescriptor.err;
        } else {
            throw new IllegalArgumentException(fd);
        }

        this.uri = uri;
        this.mediaType = MediaType.OCTET_STREAM.withCharset(URIs.getCharset(uri));
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
    public ByteSink byteSink() {
        return new ByteSink() {
            @Override
            public OutputStream openStream() throws IOException {
                return new FileOutputStream(fileDescriptor) {
                    @Override
                    public void close() throws IOException {
                        // IGNORE! Never close.
                    }
                };
            }
        };
    }

    @Override
    public ByteSource byteSource() {
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return new FileInputStream(fileDescriptor) {
                    @Override
                    public void close() throws IOException {
                        // IGNORE! Never close.
                    }
                };
            }
        };
    }
}
