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
package dev.enola.rdf.io;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;

import org.eclipse.rdf4j.common.lang.FileFormat;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/** This "bridges" RDF4j's MIME Type database to Enola's MediaType registry. */
public class RdfMediaTypes implements MediaTypeProvider {

    public static final MediaType TURTLE =
            MediaType.parse(RDFFormat.TURTLE.getDefaultMIMEType())
                    .withCharset(StandardCharsets.UTF_8);

    public static final MediaType JSON_LD =
            MediaType.parse(RDFFormat.JSONLD.getDefaultMIMEType())
                    .withCharset(StandardCharsets.UTF_8);

    private final Map<MediaType, Set<MediaType>> knownTypesWithAlternatives;
    private final Multimap<String, MediaType> extensionsToTypes;

    public RdfMediaTypes() {
        this(RDFFormat.TURTLE, RDFFormat.JSONLD);
    }

    public RdfMediaTypes(FileFormat... rdf4jFormats) {
        var extensionsToTypesBuilder = ImmutableMultimap.<String, MediaType>builder();
        var knownTypesWithAlternativesBuilder = ImmutableMap.<MediaType, Set<MediaType>>builder();

        for (var fileFormat : rdf4jFormats) {
            var primaryMediaType =
                    MediaType.parse(fileFormat.getDefaultMIMEType())
                            .withCharset(fileFormat.getCharset());
            for (var extension : fileFormat.getFileExtensions()) {
                extensionsToTypesBuilder.put("." + extension, primaryMediaType);
            }

            var altMediaTypeNames = fileFormat.getMIMETypes();
            var altMediaTypes =
                    ImmutableSet.<MediaType>builderWithExpectedSize(altMediaTypeNames.size());
            for (var alternativeMediaType : fileFormat.getMIMETypes()) {
                altMediaTypes.add(MediaType.parse(alternativeMediaType));
            }
            knownTypesWithAlternativesBuilder.put(primaryMediaType, altMediaTypes.build());
        }

        extensionsToTypes = extensionsToTypesBuilder.build();
        knownTypesWithAlternatives = knownTypesWithAlternativesBuilder.build();
    }

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return knownTypesWithAlternatives;
    }

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return extensionsToTypes;
    }
}
