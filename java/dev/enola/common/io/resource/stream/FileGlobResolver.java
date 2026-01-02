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
package dev.enola.common.io.resource.stream;

import com.google.errorprone.annotations.MustBeClosed;

import dev.enola.common.io.iri.URIs;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.util.stream.Stream;

/**
 * FileGlobResolver is a GlobResolver for a "globbed" file: IRI.
 *
 * <p>See {@link FileSystem#getPathMatcher(String)} for the full (Java) documentation of globs.
 *
 * <p>For example <code>file:/tmp/*.txt</code> (for all TXT directly in /tmp), or <code>file:/tmp/**
 * </code> (for everything under /tmp), or <code>file:/tmp/**&#47;*.txt</code> (for all TXT in
 * sub-directories of /tmp), or <code>file:/tmp/**.txt</code> (for all TXT in /tmp itself and
 * sub-directories), or similar.
 *
 * <p>It is also valid to use a file: IRI which is not actually a glob; in that case, it is
 * interpreted as just the given single file or directory.
 */
public class FileGlobResolver implements GlobResolver {

    @Override
    @MustBeClosed
    public Stream<URI> get(String globReference) {
        var globIRI = URIs.absolutify(globReference);

        if (!globIRI.startsWith("file:")) {
            throw new IllegalArgumentException("Not a file: IRI: " + globIRI);
        }

        // NB: We cannot convert globIRI to an java.net.URI, because it may contain invalid
        // characters (such as {}) which cause an URISyntaxException. We therefore only use String
        // and Path, but not URI (and have added related String instead of URI variant methods to
        // the URIs utility class).
        //
        // TODO An alternative may be to encode the curly brace ({) character using percent encoding
        // (%7B) before creating the URI, and then un-encode that again, at the appropriate moment?
        // This would best be investigated with a more comprehensive URI encoding (and IRI!) look...
        var queryParameters = URIs.getQueryMap(globIRI);
        var globPath = URIs.getFilePath(globIRI);

        try {
            return FileGlobPathWalker.walk(globPath)
                    .map(
                            path -> {
                                var pathString = path.toUri().toString();
                                var pathWithQuery = URIs.addQuery(pathString, queryParameters);
                                return URI.create(pathWithQuery);
                            });

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process 'globbed' IRI: " + globIRI, e);
        }
    }
}
