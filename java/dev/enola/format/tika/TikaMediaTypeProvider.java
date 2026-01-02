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
package dev.enola.format.tika;

import com.google.common.collect.*;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class TikaMediaTypeProvider implements MediaTypeProvider {

    private static final Set<String> EXCLUDED =
            ImmutableSet.of(
                    // GV conflicts with our GraphvizMediaType (which has UTF_8; Tika's does not)
                    ".gv");

    private static final Logger LOG = LoggerFactory.getLogger(TikaMediaTypeProvider.class);
    private static final DefaultDetector tika = new DefaultDetector();
    private final Map<MediaType, Set<MediaType>> knownTypesWithAlternatives;
    private final Multimap<String, MediaType> extensionsToTypes;

    public TikaMediaTypeProvider() {
        // NB: Similar code in TikaMediaTypesThingConverter
        var tikaMimeTypes = MimeTypes.getDefaultMimeTypes();
        var tikaMediaTypeRegistry = new AutoDetectParser().getMediaTypeRegistry();
        var tikaMediaTypes = tikaMediaTypeRegistry.getTypes();
        var n = tikaMediaTypes.size();
        var knownTypesWithAlternativesBuilder =
                ImmutableMap.<MediaType, Set<MediaType>>builderWithExpectedSize(n);
        var extensionsToTypesBuilder = ImmutableSetMultimap.<String, MediaType>builder();
        for (var tikaMediaType : tikaMediaTypes) {
            // TODO Transform tikaMediaTypeRegistry super & child types into alternatives?
            var alt = ImmutableSet.<MediaType>of();
            var guavaMediaType = TikaMediaTypes.toGuava(tikaMediaType);
            knownTypesWithAlternativesBuilder.put(guavaMediaType, alt);

            var mediaTypeName = tikaMediaType.toString();
            try {
                var tikaMimeType = tikaMimeTypes.getRegisteredMimeType(mediaTypeName);
                if (tikaMimeType == null) continue;
                for (var additionalExtension : tikaMimeType.getExtensions()) {
                    if (EXCLUDED.contains(additionalExtension)) continue;
                    // TODO This is probably not actually required? Even wrong??
                    if (!additionalExtension.startsWith("."))
                        additionalExtension = "." + additionalExtension;
                    extensionsToTypesBuilder.put(additionalExtension, guavaMediaType);
                }
            } catch (MimeTypeException e) {
                LOG.warn("MediaType not found: {}", mediaTypeName, e);
            }
        }
        knownTypesWithAlternatives = knownTypesWithAlternativesBuilder.build();
        extensionsToTypes = extensionsToTypesBuilder.build();
    }

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return knownTypesWithAlternatives;
    }

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return extensionsToTypes;
    }

    @Override
    public MediaType detect(String uri, ByteSource byteSource, MediaType original) {
        for (var excluded : EXCLUDED) if (uri.endsWith(excluded)) return original;

        var metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, uri);
        metadata.set(Metadata.CONTENT_TYPE, original.toString());

        try (var is = byteSource.openBufferedStream()) {
            var mediaType = TikaMediaTypes.toGuava(tika.detect(is, metadata));
            return mediaType;
        } catch (IOException e) {
            LOG.debug("IOException for {},", uri, e);
            return original;
        }
    }
}
