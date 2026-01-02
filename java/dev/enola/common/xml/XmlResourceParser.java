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
package dev.enola.common.xml;

import com.google.common.net.MediaType;

import dev.enola.common.convert.CatchingConverterInto;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.ReadableResource;

import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XmlResourceParser implements CatchingConverterInto<ReadableResource, DefaultHandler> {

    @Override
    public boolean convertIntoThrows(ReadableResource from, DefaultHandler into) throws Exception {
        if (!MediaTypes.normalizedNoParamsEquals(from.mediaType(), MediaType.XML_UTF_8))
            return false;

        if (from.byteSource().isEmpty()) return true;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(false);
        factory.setValidating(false);
        SAXParser saxParser = factory.newSAXParser();

        try (var is = from.byteSource().openStream()) {
            saxParser.parse(is, into);
        }
        return true;
    }
}
