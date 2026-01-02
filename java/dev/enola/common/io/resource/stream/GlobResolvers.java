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

import dev.enola.common.io.MoreFileSystems;
import dev.enola.common.io.iri.URIs;

import java.net.URI;
import java.util.stream.Stream;

public class GlobResolvers implements GlobResolver {

    private final FileGlobResolver fileGlobResolver;

    public GlobResolvers() {
        this.fileGlobResolver = new FileGlobResolver();
    }

    @Override
    @MustBeClosed
    public Stream<URI> get(String globReference) {
        var globIRI = URIs.absolutify(globReference);
        if (MoreFileSystems.URI_SCHEMAS.contains(URIs.getScheme(globIRI)))
            return fileGlobResolver.get(globIRI);
        else return Stream.of(URI.create(globIRI));
    }
}
