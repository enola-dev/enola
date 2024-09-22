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
import com.google.common.collect.Multimaps;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;
import dev.enola.common.io.resource.AbstractResource;
import dev.enola.common.io.resource.ReadableResource;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
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
        // for (var detector : tika.getDetectors()) {}
        // TODO Implement this... but how? Looking at
        // https://github.com/apache/tika/blob/7f7b11b773bdfefe84e1a343f2c07ed2788986ca/tika-app/src/main/java/org/apache/tika/cli/TikaCLI.java#L757
        // the TikaDetectors don't appear to expose which MediaType they handle?
        knownTypesWithAlternatives = Map.of();
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
                    return convert(tika.detect(is, metadata));
                }

            } else {
                return convert(tika.detect(InputStream.nullInputStream(), metadata));
            }

        } catch (IOException e) {
            LOG.debug("IOException for {},", abstractResource.uri(), e);
            return Optional.empty();
        }
    }

    private Optional<MediaType> convert(org.apache.tika.mime.MediaType tikaMediaType) {
        // Not efficient: return Optional.of(MediaType.parse(tikaMediaType.toString()));
        var guavaMediaType = MediaType.create(tikaMediaType.getType(), tikaMediaType.getSubtype());
        var multiMap = Multimaps.forMap(tikaMediaType.getParameters());
        guavaMediaType = guavaMediaType.withParameters(multiMap);
        return Optional.of(guavaMediaType);
    }
}
