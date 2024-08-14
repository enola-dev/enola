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
package dev.enola.rdf.io;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.common.io.resource.WritableResource;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;

/**
 * Writes RDFJ4j {@link Statement}s (like {@link org.eclipse.rdf4j.model.Model}) into a {@link
 * WritableResource}.
 */
class RdfWriterConverter implements ConverterInto<Iterable<Statement>, WritableResource> {

    @Override
    public boolean convertInto(Iterable<Statement> from, WritableResource into)
            throws ConversionException {
        var writerFormat =
                Rio.getWriterFormatForMIMEType(into.mediaType().withoutParameters().toString());
        String baseURI = into.uri().toString();
        if (!writerFormat.isPresent()) {
            writerFormat = Rio.getWriterFormatForFileName(baseURI);
        }
        if (writerFormat.isPresent()) {
            try {
                try (Writer writer = into.charSink().openBufferedStream()) {
                    Rio.write(from, writer, baseURI, writerFormat.get());
                    return true;
                }
            } catch (IOException | UnsupportedRDFormatException | URISyntaxException e) {
                throw new ConversionException("Failed writing to: " + into, e);
            }
        }
        // NOT throw new ConversionException("No RDFFormat for: " + from);
        return false;
    }
}
