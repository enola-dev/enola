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

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.thing.Thing;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.repo.ThingsBuilder;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URI;

public class TikaThingConverter implements UriIntoThingConverter {

    private static final Logger LOG = LoggerFactory.getLogger(TikaThingConverter.class);

    private static final AutoDetectParser parser = new AutoDetectParser();
    private final ResourceProvider rp;

    public TikaThingConverter(ResourceProvider resourceProvider) {
        this.rp = resourceProvider;
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

        // TODO Content?
        // For debugging, e.g. use:
        // BufferedWriter stdOut = new BufferedWriter(new OutputStreamWriter(System.out));
        // BodyContentHandler handler = new BodyContentHandler(stdOut);
        ContentHandler handler = new DefaultHandler();

        try (var is = resource.byteSource().openBufferedStream()) {
            Metadata metadata = new Metadata();
            parser.parse(is, handler, metadata);
            var thing = thingsBuilder.get(resource.uri().toString());
            convertMetadata(metadata, thing);
            return true;

        } catch (TikaException | SAXException e) {
            throw new ConversionException("Tika could not convert: " + resource, e);
        }
    }

    private void convertMetadata(Metadata metadata, Thing.Builder<?> thing) {
        // TODO Do better IRI conversions of some well-known names
        // ...

        // Fallback
        for (var name : metadata.names()) {
            var value =
                    metadata.isMultiValued(name)
                            ? ImmutableSet.copyOf(metadata.getValues(name))
                            : metadata.get(name);

            var predicate = "https://enola.dev/tika/" + URIs.encode(name);
            thing.set(predicate, value);
        }
    }
}
