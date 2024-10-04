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

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.iri.URIs;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Resource for URIs "fd:0?charset=ASCII" (STDIN), "fd:1?charset=UTF-8" (STDOUT),
 * "fd:2?charset=UTF-16BE" STDERR. Implementation uses {@link FileDescriptor}.
 */
public class FileDescriptorResource extends BaseResource implements Resource {

    public static final String STDOUT = "fd:1?charset=UTF-8";

    public static final URI STDOUT_URI = URI.create(STDOUT);

    public static class Provider implements ResourceProvider {

        @Override
        public Resource getResource(URI uri) {
            if ("fd".equals(uri.getScheme())) return new FileDescriptorResource(uri);
            else return null;
        }
    }

    private final FileDescriptor fileDescriptor;

    public FileDescriptorResource(URI uri) {
        // TODO This doesn't seem right... why hard-code this?!
        super(uri, MediaType.PLAIN_TEXT_UTF_8.withCharset(Charset.defaultCharset()));

        if (!"fd".equals(uri.getScheme())) {
            throw new IllegalArgumentException(uri.toString());
        }

        var fd = URIs.getPath(uri);
        if (fd.startsWith("0")) {
            fileDescriptor = FileDescriptor.in;
        } else if (fd.startsWith("1")) {
            fileDescriptor = FileDescriptor.out;
        } else if (fd.startsWith("2")) {
            fileDescriptor = FileDescriptor.err;
        } else {
            throw new IllegalArgumentException(fd);
        }
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
