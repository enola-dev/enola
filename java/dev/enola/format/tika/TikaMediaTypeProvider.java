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
package dev.enola.format.tika;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;
import dev.enola.common.io.resource.AbstractResource;
import dev.enola.common.io.resource.ReadableResource;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@AutoService(MediaTypeProvider.class)
public class TikaMediaTypeProvider implements MediaTypeProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TikaMediaTypeProvider.class);
    private static final DefaultDetector tika = new DefaultDetector();
    private final Map<MediaType, Set<MediaType>> knownTypesWithAlternatives;
    private final Map<String, MediaType> extensionsToTypes;

    public TikaMediaTypeProvider() {
        var tikaMediaTypeRegistry = new AutoDetectParser().getMediaTypeRegistry();
        var tikaMediaTypes = tikaMediaTypeRegistry.getTypes();
        var knownTypesWithAlternativesBuilder =
                ImmutableMap.<MediaType, Set<MediaType>>builderWithExpectedSize(
                        tikaMediaTypes.size());
        for (var tikaMediaType : tikaMediaTypes) {
            // TODO Transform tikaMediaTypeRegistry super & child types into alternative?
            var alt = ImmutableSet.<MediaType>of();
            knownTypesWithAlternativesBuilder.put(convert(tikaMediaType), alt);
        }
        knownTypesWithAlternatives = knownTypesWithAlternativesBuilder.build();
        // TODO Where does Tika hide its supported file extensions?!
        extensionsToTypes = Map.of();
    }

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return knownTypesWithAlternatives;
    }

    @Override
    public Map<String, MediaType> extensionsToTypes() {
        return extensionsToTypes;
    }

    @Override
    public Optional<MediaType> detect(AbstractResource abstractResource) {
        var metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, abstractResource.uri().toString());
        metadata.set(Metadata.CONTENT_TYPE, abstractResource.mediaType().toString());

        try {
            if (abstractResource instanceof ReadableResource readableResource) {
                try (var is = readableResource.byteSource().openBufferedStream()) {
                    return Optional.of(convert(tika.detect(is, metadata)));
                }

            } else {
                return Optional.of(convert(tika.detect(InputStream.nullInputStream(), metadata)));
            }

        } catch (IOException e) {
            LOG.debug("IOException for {},", abstractResource.uri(), e);
            return Optional.empty();
        }
    }

    private MediaType convert(org.apache.tika.mime.MediaType tikaMediaType) {
        // Not efficient: return Optional.of(MediaType.parse(tikaMediaType.toString()));
        var guavaMediaType = MediaType.create(tikaMediaType.getType(), tikaMediaType.getSubtype());
        var multiMap = Multimaps.forMap(tikaMediaType.getParameters());
        return guavaMediaType.withParameters(multiMap);
    }
}
