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

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.repo.ThingsBuilder;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URI;

public class TikaThingConverter implements UriIntoThingConverter {

    private static final AutoDetectParser parser = new AutoDetectParser();
    private final ResourceProvider rp;

    public TikaThingConverter(ResourceProvider resourceProvider) {
        this.rp = resourceProvider;
    }

    @Override
    public boolean convertInto(URI from, ThingsBuilder into)
            throws ConversionException, IOException {

        var resource = rp.getReadableResource(from);
        if (resource.byteSource().isEmpty()) return false;

        // TODO Remove temporary BodyContentHandler & Metadata, only for exploration
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();

        try (var is = resource.byteSource().openBufferedStream()) {
            parser.parse(is, handler, metadata);
            // TODO Thingify instead of this...
            throw new RuntimeException(handler.toString());

        } catch (TikaException | SAXException e) {
            throw new ConversionException("Tika could not convert: " + from, e);
        }

        // TODO return false;
    }
}
