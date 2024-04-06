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

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * FileGlobResourceProvider is a GlobResourceProvider for a "globbed" file: IRI.
 *
 * <p>For example <tt>file:/tmp/*.txt</tt> (for all TXT directly in /tmp), or
 * <tt>file:/tmp/**&#47;*.txt</tt> (for all TXT in sub-directories of /tmp, but excluding /tmp
 * itself), or <tt>file:/tmp/**.txt</tt> (for all TXT in /tmp itself and sub-directories), or
 * similar.
 */
public class FileGlobResourceProvider implements GlobResourceProvider {

    private final ResourceProvider fileResourceProvider = new ResourceProviders();

    @Override
    public Stream<ReadableResource> get(String globIRI) {
        var globURI = URI.create(globIRI);
        var globPath = Path.of(globURI);

        try {
            return FileGlobPathWalker.walk(globPath)
                    .map(path -> fileResourceProvider.getReadableResource(path.toUri()));

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process 'globbed' IRI: " + globIRI, e);
        }
    }
}
