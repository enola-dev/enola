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
package dev.enola.common.canonicalize;

import static dev.enola.common.io.mediatype.MediaTypes.normalizedNoParamsEquals;

import com.google.common.net.MediaType;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.html.HTML;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CharResourceConverter;
import dev.enola.common.io.resource.convert.IdempotentCopyingResourceNonConverter;
import dev.enola.common.io.resource.convert.ResourceConverter;
import dev.enola.common.xml.XML;
import dev.enola.common.yamljson.JSON;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Canonicalizer implements ResourceConverter {

    // TODO Unify this with Rosetta!! Because of the dependency tree, Rosetta first
    //  needs to be made AutoService-based, then moved here, then merged with this.

    // TODO Implement https://www.w3.org/TR/rdf-canon/

    public static final String PRETTY_QUERY_PARAMETER = "pretty";

    public static void canonicalize(ReadableResource in, WritableResource out, boolean pretty)
            throws IOException {
        var inMT = in.mediaType();
        // TODO MediaTypes should normalize based on +json/xml-like subtype endings; with "primary"?
        var isJSON = normalizedNoParamsEquals(inMT, MediaType.JSON_UTF_8);
        var hasJSON = inMT.subtype().endsWith("+json"); // e.g. "application/ld+json" et al.
        if (isJSON || hasJSON) {
            var json = in.charSource().read();
            var canonicalized = JSON.canonicalize(json, pretty);

            // Force UTF-8, see https://www.rfc-editor.org/rfc/rfc8785#name-utf-8-generation
            // This intentionally completely ignores the WritableResource out's mediaType charset.
            out.byteSink().write(canonicalized.getBytes(StandardCharsets.UTF_8));

        } else if (normalizedNoParamsEquals(inMT, MediaType.XML_UTF_8)
                || inMT.subtype().endsWith("+xml")) {
            XML.canonicalize(in, out);

        } else if (normalizedNoParamsEquals(inMT, MediaType.HTML_UTF_8)) {
            var outCharset = out.mediaType().charset().or(StandardCharsets.UTF_8);
            out.charSink().write(HTML.canonicalize(in, outCharset));

            // TODO } else if (normalizedNoParamsEquals(inMT, RdfMediaTypes.TURTLE)) {

            // TODO } else if (normalizedNoParamsEquals(inMT, RdfMediaTypes.JSON_LD)) {

        } else {
            // TODO new Rosetta().convertIntoOrThrow(in, out);
            if (!(new CharResourceConverter().convertIntoThrows(in, out)))
                new IdempotentCopyingResourceNonConverter().convertIntoThrows(in, out);
        }
    }

    @Override
    public boolean convertInto(ReadableResource from, WritableResource into)
            throws ConversionException, IOException {

        var outQueryMap = URIs.getQueryMap(into.uri());
        var pretty = outQueryMap.get(PRETTY_QUERY_PARAMETER) != null;
        canonicalize(from, into, pretty);
        return true;
    }
}
