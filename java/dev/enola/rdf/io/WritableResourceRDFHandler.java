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
package dev.enola.rdf.io;

import dev.enola.common.io.resource.WritableResource;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Optional;

public class WritableResourceRDFHandler implements RDFHandler, Closeable {

    public static Optional<WritableResourceRDFHandler> create(WritableResource resource)
            throws IOException {
        try {
            String baseURI = resource.uri().toString();
            var mediaType = resource.mediaType().withoutParameters().toString();
            var writerFormat = Rio.getWriterFormatForMIMEType(mediaType);
            // TODO See https://github.com/eclipse-rdf4j/rdf4j/issues/5272:
            if (mediaType.equals("text/plain")
                    && writerFormat.isPresent()
                    && writerFormat.get().equals(RDFFormat.NTRIPLES))
                writerFormat = Optional.empty();
            if (writerFormat.isEmpty()) writerFormat = Rio.getWriterFormatForFileName(baseURI);
            if (writerFormat.isEmpty()) return Optional.empty();

            var ioWriter = resource.charSink().openBufferedStream();
            var rdfWriter = Rio.createWriter(writerFormat.get(), ioWriter, baseURI);
            var writerConfig = new WriterConfig();
            writerConfig.set(BasicWriterSettings.BASE_DIRECTIVE, false);
            writerConfig.set(BasicWriterSettings.INLINE_BLANK_NODES, true);
            rdfWriter.setWriterConfig(writerConfig);

            var it = new WritableResourceRDFHandler(rdfWriter, ioWriter);
            rdfWriter.startRDF();
            return Optional.of(it);

        } catch (URISyntaxException e) {
            throw new IOException("URISyntaxException: " + resource, e);
        }
    }

    private final RDFWriter /* IS-A RDFHandler */ rdfWriter;
    private final Writer ioWriter;

    private WritableResourceRDFHandler(RDFWriter rdfWriter, Writer ioWriter)
            throws IOException, URISyntaxException {
        this.rdfWriter = rdfWriter;
        this.ioWriter = ioWriter;
    }

    @Override
    public void close() throws IOException {
        rdfWriter.endRDF();
        ioWriter.close();
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        // SUPPRESS rdfWriter.startRDF();
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        // SUPPRESS rdfWriter.endRDF();
    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        rdfWriter.handleNamespace(prefix, uri);
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        rdfWriter.handleStatement(st);
    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {
        rdfWriter.handleComment(comment);
    }
}
