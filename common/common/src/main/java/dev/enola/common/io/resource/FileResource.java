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
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeDetector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

/**
 * {@link Resource} for a file at a {@link Path} on a {@link FileSystem}.
 *
 * <p>Note that a {@link Path} object instance is associated to a FileSystem, see {@link
 * Path#getFileSystem()}. But in its text-form ({@link Path#of(String, String...)} and {@link
 * Path#toString()}) this association is lost, and it should thus be avoided. In its URI-form
 * ({@link Path#of(URI)} and {@link Path#toUri()}) this is preserved; typically via the "scheme"
 * (and possibly the "authority") component/s of an URI. So a path in text does not identify a
 * filesystem, whereas an URI can. While the "file:" scheme is obviously the most well-known and
 * common one, the JVM actually can and does support others, notably the "jar:file:" one. To avoid
 * related confusions, this class intentionally only offers constructors which take {@link URI}
 * instead of {@link Path} arguments.
 */
public class FileResource extends BaseResource implements Resource {

    private static final MediaTypeDetector mtd = new MediaTypeDetector();

    private static final OpenOption[] EMPTY_OPTIONS = new OpenOption[0];

    private final URI uri; // TODO Move to BaseResource...
    private final Path path;
    private final MediaType mediaType;
    private final OpenOption[] openOptions;

    private static Path pathFromURI(URI uri) {
        // TODO Replace this with return Path.of(uri); but it needs more work...
        var path = URIs.getPath(uri);
        var scheme = uri.getScheme();
        if ("file".equals(scheme)) {
            return FileSystems.getDefault().getPath(path);
        } else
            try {
                URI fsURI = new URI(scheme, uri.getAuthority(), "", null, null);
                var fs = FileSystems.getFileSystem(fsURI);
                return fs.getPath(path);
            } catch (URISyntaxException e) {
                // This is rather unexpected...
                throw new IllegalStateException(
                        "Failed to create FileSystem Authority URI: " + uri.toString(), e);
            }
    }

    public FileResource(URI uri, MediaType mediaType, OpenOption... openOptions) {
        this.uri = uri;
        this.path = pathFromURI(uri);
        this.openOptions = safe(openOptions);
        // Here we intentionally do not do something like this, because the arg takes precedence:
        // mtd.detect(
        //         mediaType.toString(),
        //         mediaType.charset().transform(cs -> cs.name()).orNull(),
        //         uri());
        this.mediaType = requireNonNull(mediaType);
    }

    public FileResource(URI uri, OpenOption... openOptions) {
        this.uri = uri;
        this.path = pathFromURI(uri);
        this.openOptions = safe(openOptions);
        this.mediaType = mtd.detectAlways(this);
    }

    private static OpenOption[] safe(OpenOption[] openOptions) {
        if (openOptions.length == 0) return EMPTY_OPTIONS;
        else return Arrays.copyOf(openOptions, openOptions.length);
    }

    @Override
    public URI uri() {
        return uri; // NOT path.toUri();
    }

    public Path path() {
        return path;
    }

    @Override
    public MediaType mediaType() {
        return mediaType;
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

    @Override
    public Optional<Instant> lastModifiedIfKnown() {
        try {
            return Optional.of(Files.getLastModifiedTime(path).toInstant());
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
