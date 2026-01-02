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
package dev.enola.model.enola.mediatype;

import com.google.common.base.Strings;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.format.tika.TikaMediaTypes;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.repo.ThingRepositoryStore;

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
public class TikaMediaTypesThingConverter implements UriIntoThingConverter {

    // TODO Make https://enola.dev/fileExtensions be links, not text?

    private static final Logger LOG = LoggerFactory.getLogger(TikaMediaTypesThingConverter.class);

    public static final URI IRI = URI.create("enola:TikaMediaTypes");

    @Override
    public boolean convertInto(URI from, ThingRepositoryStore into)
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
                var iri = toIRI(tikaMediaType);
                MediaType.Builder thing =
                        into.getBuilder(iri, MediaType.class, MediaType.Builder.class);
                thing.addType("https://enola.dev/MediaType");
                thing.mediaType(tikaMimeType.getName());

                var label = tikaMimeType.getAcronym();
                if (!Strings.isNullOrEmpty(label)) thing.label(label);
                else thing.label(tikaMimeType.getName());

                thing.comment(tikaMimeType.getDescription());
                thing.addAllFileExtensions(tikaMimeType.getExtensions());
                thing.addAllSeeAlso(
                        tikaMimeType.getLinks().stream().map(uri -> uri.toString()).toList());

                // TODO var uniformTypeIdentifier = tikaMimeType.getUniformTypeIdentifier();
                // TODO var hasMagic = tikaMimeType.hasMagic();

                // TODO Tika hard-codes :( a few special cases, and doesn't e.g. do +json...
                var superType = tikaMediaTypeRegistry.getSupertype(tikaMediaType);
                if (superType != null) thing.parentIRI(toIRI(superType));

                // tikaMediaType.getBaseType() is a superset of getSupertype()

                // TODO Making the following a 1 liner...
                // TODO Remove this once children are automagically set by generic Inference!!
                // TODO Uncomment, once GraphvizGenerator more nicely coalesces parent & children
                /*
                var children = tikaMediaTypeRegistry.getChildTypes(tikaMediaType);
                if (!children.isEmpty()) {
                    var childrenIRI = ImmutableSet.<Link>builderWithExpectedSize(children.size());
                    for (var child : children) {
                        childrenIRI.add(new Link(toIRI(child)));
                    }
                    thing.childrenIRI(childrenIRI.build());
                }
                */

                into.store(thing.build());

            } catch (MimeTypeException e) {
                LOG.warn("MediaType not found: {}", mediaTypeName, e);
            }
        }
        return true;
    }

    private String toIRI(org.apache.tika.mime.MediaType tikaMediaType) {
        return MediaTypes.toIRI(TikaMediaTypes.toGuava(tikaMediaType));
    }
}
