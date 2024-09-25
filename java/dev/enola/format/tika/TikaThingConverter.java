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

import com.google.common.collect.ImmutableSet;

import dev.enola.common.StringBuilderWriter;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.iri.namespace.NamespaceConverter;
import dev.enola.common.io.iri.namespace.NamespaceConverterWithRepository;
import dev.enola.common.io.iri.namespace.NamespaceRepositoryEnolaDefaults;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.thing.Thing;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.repo.ThingsBuilder;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TikaThingConverter implements UriIntoThingConverter {

    private static final AutoDetectParser parser = new AutoDetectParser();
    private final ResourceProvider rp;

    private final NamespaceConverter namespaceConverter;

    public TikaThingConverter(ResourceProvider resourceProvider) {
        this.rp = resourceProvider;
        this.namespaceConverter =
                new NamespaceConverterWithRepository(NamespaceRepositoryEnolaDefaults.INSTANCE);
    }

    @Override
    public boolean convertInto(URI from, ThingsBuilder thingsBuilder)
            throws ConversionException, IOException {
        var resource = rp.getReadableResource(from);
        if (resource == null) return false;
        return convertInto(resource, thingsBuilder);
    }

    public boolean convertInto(ReadableResource resource, ThingsBuilder thingsBuilder)
            throws ConversionException, IOException {
        if (resource.byteSource().isEmpty()) return false;

        Writer sw = new StringBuilderWriter();
        BodyContentHandler handler = new BodyContentHandler(sw);

        try (var is = resource.byteSource().openBufferedStream()) {
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            // TODO How to pass e.g. current Locale from TLC, e.g. for XLS parsing?
            parser.parse(is, handler, metadata, parseContext);
            var thing = thingsBuilder.getBuilder(resource.uri().toString());
            convertMetadata(metadata, thing);
            var text = sw.toString().trim();
            if (!text.isEmpty()) thing.set("https://enola.dev/content-as-text", text);
            return true;

        } catch (TikaException | SAXException e) {
            throw new ConversionException("Tika could not convert: " + resource, e);
        }
    }

    private void convertMetadata(Metadata metadata, Thing.Builder<?> thing) {
        final var properties = new HashMap<String, Object>();
        final var names = new ArrayList<>(List.of(metadata.names()));
        while (!names.isEmpty()) {
            final var name = names.remove(0);
            if (name.startsWith("X-TIKA")) continue;

            final var value =
                    metadata.isMultiValued(name)
                            ? ImmutableSet.copyOf(metadata.getValues(name))
                            : metadata.get(name);

            final var toClean = CleanMetadata.ALL.get(name);
            if (toClean != null) {
                var iri = toClean.iri();
                if (iri != null) properties.put(iri, value);

                var removeNames = List.of(toClean.removeNames());
                for (var removeName : removeNames) {
                    names.remove(removeName);
                    properties.remove(tikaMetadataNameToEnolaIRI(removeName));
                }

            } else {
                var iri = namespaceConverter.toIRI(name);
                if (!iri.equals(name)) properties.put(iri, value);
                else properties.put(tikaMetadataNameToEnolaIRI(name), value);
            }
        }
        properties.forEach(thing::set);
    }

    private String tikaMetadataNameToEnolaIRI(String name) {
        return "https://enola.dev/tika/" + URIs.encode(name);
    }
}
