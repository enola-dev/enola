/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * FileGlobResourceProvider is a GlobResourceProvider for a "globbed" file: IRI.
 *
 * <p>See {@link FileSystem#getPathMatcher(String)} for the full (Java) documentation of globs.
 *
 * <p>For example <tt>file:/tmp/*.txt</tt> (for all TXT directly in /tmp), or
 * <tt>file:/tmp/**&#47;*.txt</tt> (for all TXT in sub-directories of /tmp, but excluding /tmp
 * itself), or <tt>file:/tmp/**.txt</tt> (for all TXT in /tmp itself and sub-directories), or
 * similar.
 *
 * <p>It is also valid to use a file: IRI which is not actually a glob; in that case, it is
 * interpreted as just the given single file (if it is a file, or empty if that's a directory).
 */
public class FileGlobResourceProvider implements GlobResourceProvider {

    private final ResourceProvider fileResourceProvider = new ResourceProviders();

    @Override
    @MustBeClosed
    public Stream<ReadableResource> get(String globIRI) {
        if (!globIRI.startsWith("file:")) {
            throw new IllegalArgumentException("Not a file: IRI: " + globIRI);
        }
        var globPath = Path.of(globIRI.substring("file:".length()));

        try {
            return FileGlobPathWalker.walk(globPath)
                    .filter(path -> !path.toFile().isDirectory())
                    .map(path -> fileResourceProvider.getReadableResource(path.toUri()));

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process 'globbed' IRI: " + globIRI, e);
        }
    }
}
