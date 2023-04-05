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

import com.google.common.base.Charsets;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeDetector;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;

public class FileResource implements Resource {

    private static final MediaTypeDetector mtd = new MediaTypeDetector();

    private final Path path;
    private final Charset charset;
    private final OpenOption[] openOptions;

    public FileResource(Path path, OpenOption... openOptions) {
        this(path, Charsets.UTF_8, openOptions);
    }

    public FileResource(Path path, Charset charset, OpenOption... openOptions) {
        this.path = path;
        this.charset = charset;
        this.openOptions = Arrays.copyOf(openOptions, openOptions.length);
    }

    @Override
    public URI uri() {
        return path.toUri();
    }

    @Override
    public MediaType mediaType() {
        return mtd.detect(null, charset.name(), uri());
    }

    @Override
    public ByteSink byteSink() {
        return MoreFiles.asByteSink(path, openOptions);
    }

    @Override
    public ByteSource byteSource() {
        return MoreFiles.asByteSource(path, openOptions);
    }

    @Override
    public String toString() {
        return "FileResource{path=" + path + ", charset=" + charset + '}';
    }
}
