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
import dev.enola.common.convert.ConverterInto;
import dev.enola.common.io.resource.ReadableResource;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.Reader;

public class RdfReaderConverterInto implements ConverterInto<ReadableResource, RDFHandler> {

    @Override
    public boolean convertInto(ReadableResource from, RDFHandler into) throws ConversionException {
        var parserFormat = Rio.getParserFormatForMIMEType(from.mediaType().toString());
        if (!parserFormat.isPresent()) {
            parserFormat = Rio.getParserFormatForFileName(from.uri().toString());
        }
        if (parserFormat.isPresent()) {
            String baseURI = from.uri().toString();
            try (Reader reader = from.charSource().openStream()) {
                var parser = Rio.createParser(parserFormat.get());
                parser.setRDFHandler(into);
                parser.parse(reader, baseURI);
                return true;
            } catch (IOException e) {
                throw new ConversionException("Failing reading from : " + from, e);
            } catch (RDFParseException e) {
                throw new ConversionException(
                        "RDFParseException reading from resource",
                        from.uri(),
                        e.getLineNumber(),
                        e.getColumnNumber(),
                        e);
            }
        }
        return false;
    }
}
