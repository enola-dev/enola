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

import dev.enola.common.CloseableIterator;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * FileGlobResourceProvider is a GlobResourceProvider for a "globbed" file: IRI.
 *
 * <p>For example <tt>file:**&#47;*.txt</tt> (relative), or <tt>file:/tmp/**&#47;*.txt</tt>
 * (absolute; or <tt>file:///tmp/**&#47;*.txt</tt>), or something similar to that.
 */
public class FileGlobResourceProvider implements GlobResourceProvider {

    private final ResourceProvider fileResourceProvider = new ResourceProviders();

    @Override
    public CloseableIterator<ReadableResource> get(String globIRI) {
        if (!globIRI.startsWith("file:")) {
            throw new IllegalArgumentException(
                    "IRI can be relative, but must start with scheme file: " + globIRI);
        }
        var globPath = globIRI.substring("file:".length() - 1);

        try {
            var starPos = globPath.indexOf('*');
            if (starPos == -1)
                throw new IllegalArgumentException(
                        "TODO: Implement support for non-* glob: " + globIRI);

            Path basePath = Path.of(globPath.substring(0, starPos - 1));
            String glob = globPath.substring(starPos);
            return new DirectoryStreamCloseableIterator(Files.newDirectoryStream(basePath, glob));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process 'globbed' IRI: " + globIRI, e);
        }
    }

    private class DirectoryStreamCloseableIterator implements CloseableIterator<ReadableResource> {
        private final DirectoryStream<Path> directoryStream;
        private final Iterator<Path> iterator;

        public DirectoryStreamCloseableIterator(DirectoryStream<Path> directoryStream) {
            this.directoryStream = directoryStream;
            this.iterator = directoryStream.iterator();
        }

        @Override
        public void close() throws IOException {
            directoryStream.close();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public ReadableResource next() {
            var path = iterator.next();
            var fileURI = path.toFile().toURI();
            return fileResourceProvider.getReadableResource(fileURI);
        }
    }
}
