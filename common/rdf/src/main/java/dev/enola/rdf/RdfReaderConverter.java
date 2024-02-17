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
package dev.enola.rdf;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.common.io.resource.ReadableResource;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.Reader;

public class RdfReaderConverter implements Converter<ReadableResource, Model> {

    @Override
    public Model convert(ReadableResource input) throws ConversionException {
        var parserFormat = Rio.getParserFormatForMIMEType(input.mediaType().toString());
        if (!parserFormat.isPresent()) {
            parserFormat = Rio.getParserFormatForFileName(input.uri().toString());
        }
        if (parserFormat.isPresent()) {
            String baseURI = input.uri().toString();
            try (Reader reader = input.charSource().openStream()) {
                return Rio.parse(reader, baseURI, parserFormat.get());
            } catch (IOException e) {
                throw new ConversionException("Failing reading from : " + input, e);
            }
        }
        throw new ConversionException("No RDFFormat for: " + input);
    }
}
