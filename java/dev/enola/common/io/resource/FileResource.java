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
import com.google.common.io.MoreFiles;
import com.google.common.net.MediaType;

import dev.enola.common.io.MoreFileSystems;
import dev.enola.common.io.iri.URIs;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Arrays;

/**
 * {@link Resource} for a file (not a directory) at a {@link Path} on a {@link FileSystem}.
 *
 * <p>Note that a {@link Path} object instance is associated to a FileSystem, see {@link
 * Path#getFileSystem()}. But in its text-form ({@link Path#of(String, String...)} and {@link
 * Path#toString()}) this association is lost, and it should thus be avoided. In its URI-form
 * ({@link Path#of(URI)} and {@link Path#toUri()}) this is preserved; typically via the "scheme"
 * (and possibly the "authority") component/s of an URI. So a path in text does not identify a
 * filesystem, whereas an URI can. While the "file:" scheme is obviously the most well-known and
 * common one, the JVM actually can and does support others, notably the "jar:file:" one. To avoid
 * related confusions, this class intentionally only offers constructors which take {@link URI}
 * instead of {@link Path} (or exposing {@link File}) arguments.
 */
public class FileResource extends BaseResource implements Resource {
    // TODO Rename FileResource to FileSystemPathResource!

    public static class Provider implements ResourceProvider {

        private final @Nullable File baseFile;

        public Provider(File baseFile) {
            this.baseFile = baseFile;
        }

        public Provider() {
            this.baseFile = null;
        }

        @Override
        public Resource getResource(URI uri) {
            // URIs like "jar:file:/tmp/libmodels.jar!/enola.dev/properties.ttl" are not supported
            // here. The UrlResource accepts jar: scheme though - and can read (but not write) them.
            if ("jar".equals(uri.getScheme())) return null;

            if (MoreFileSystems.URI_SCHEMAS.contains(uri.getScheme())
                    && isValid(URIs.getFilePath(uri))) return new FileResource(uri);

            // NB: There's very similar logic in ClasspathResource
            if (uri.getScheme() == null && baseFile != null) {
                if (uri.toString().contains(".."))
                    throw new IllegalArgumentException(uri.toString());
                return new ReadableButNotWritableDelegatingResource(
                        new FileResource(new File(baseFile, uri.toString()).toURI()));
            }

            return null;
        }
    }

    private static final OpenOption[] EMPTY_OPTIONS = new OpenOption[0];

    private final Path path;
    private final OpenOption[] openOptions;

    public FileResource(URI uri, MediaType mediaType, OpenOption... openOptions) {
        super(uri, mediaType);
        this.path = checkValid(URIs.getFilePath(uri));
        this.openOptions = safe(openOptions);
    }

    public FileResource(URI uri, OpenOption... openOptions) {
        super(uri, MoreFiles.asByteSource(URIs.getFilePath(uri), openOptions));
        this.path = checkValid(URIs.getFilePath(uri));
        this.openOptions = safe(openOptions);
    }

    private static Path checkValid(Path path) {
        if (isValid(path)) return path;
        throw new IllegalArgumentException(
                "Exists, but is a directory; thus cannot be a Resource: " + path);
    }

    private static boolean isValid(Path path) {
        if (Files.notExists(path)) return true;
        return !Files.isDirectory(path);
    }

    private static OpenOption[] safe(OpenOption[] openOptions) {
        if (openOptions.length == 0) return EMPTY_OPTIONS; // skipcq: JAVA-S1049
        else return Arrays.copyOf(openOptions, openOptions.length);
    }

    @Override
    public ByteSink byteSink() {
        var parentDirectoryPath = path.getParent();
        if (parentDirectoryPath != null) {
            try {
                if (!Files.exists(parentDirectoryPath))
                    Files.createDirectories(parentDirectoryPath);
            } catch (IOException e) {
                return new ErrorByteSink(e);
            }
        }
        return MoreFiles.asByteSink(path, openOptions);
    }

    @Override
    public ByteSource byteSource() {
        return MoreFiles.asByteSource(path, openOptions);
    }
}
