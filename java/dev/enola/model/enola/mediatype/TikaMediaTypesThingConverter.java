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
package dev.enola.model.enola.mediatype;

import dev.enola.common.convert.ConversionException;
import dev.enola.thing.Thing;
import dev.enola.thing.io.TypedUriIntoThingConverter;
import dev.enola.thing.repo.TypedThingsBuilder;

import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Converts Tika Media Types into Enola Things (AKA RDF / TTL).
 *
 * <p>Tika reads these from its (builtin) <a
 * href="https://github.com/apache/tika/blob/116edb30dc5fd26770216ccffcf873f4952a5c2a/tika-core/src/main/resources/org/apache/tika/mime/tika-mimetypes.xml">MIME
 * Types XML</a> file which corresponds to the <a
 * href="https://freedesktop.org/wiki/Specifications/shared-mime-info-spec/">freedesktop.org MIME
 * Info Spec</a>.
 */
public class TikaMediaTypesThingConverter
        implements TypedUriIntoThingConverter<MediaType, MediaType.Builder> {

    private static final Logger LOG = LoggerFactory.getLogger(TikaMediaTypesThingConverter.class);

    public static final URI IRI = URI.create("enola:TikaMediaTypes");

    @Override
    public boolean convertInto(URI from, TypedThingsBuilder<MediaType, MediaType.Builder> into)
            throws ConversionException, IOException {
        if (!IRI.equals(from)) return false;

        // NB: Similar code in TikaMediaTypeProvider
        var tikaMimeTypes = MimeTypes.getDefaultMimeTypes();
        var tikaMediaTypeRegistry = new AutoDetectParser().getMediaTypeRegistry();
        var tikaMediaTypes = tikaMediaTypeRegistry.getTypes();
        for (var tikaMediaType : tikaMediaTypes) {
            var mediaTypeName = tikaMediaType.toString();
            try {
                var tikaMimeType = tikaMimeTypes.getRegisteredMimeType(mediaTypeName);
                var name = tikaMimeType.getName();
                var iri = "https://enola.dev/mediaType/" + name;
                Thing.Builder debug =
                        into.getBuilder(iri, MediaType.Builder.class, MediaType.class);
                MediaType.Builder thing = (MediaType.Builder) debug;

                thing.label(tikaMimeType.getAcronym());
                thing.comment(tikaMimeType.getDescription());
                // TODO thing.addAllFileExtensions(tikaMimeType.getExtensions());
                thing.addFileExtension(tikaMimeType.getExtension());
                // TODO thing.addAllSeeAlso(tikaMimeType.getLinks())

                // TODO var uniformTypeIdentifier = tikaMimeType.getUniformTypeIdentifier();
                // TODO var hasMagic = tikaMimeType.hasMagic();

                tikaMediaTypeRegistry.getChildTypes(tikaMediaType);
                tikaMediaTypeRegistry.getSupertype(tikaMediaType);

                var baseType = tikaMediaType.getBaseType();
                if (!baseType.equals(tikaMediaType)) {}

            } catch (MimeTypeException e) {
                LOG.warn("MediaType not found: {}", mediaTypeName, e);
            }
        }
        return true;
    }
}
