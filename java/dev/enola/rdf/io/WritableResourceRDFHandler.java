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

import dev.enola.common.io.resource.WritableResource;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Optional;

public class WritableResourceRDFHandler implements RDFHandler, Closeable {

    public static Optional<WritableResourceRDFHandler> create(WritableResource resource)
            throws IOException, URISyntaxException {
        // NB: Similar code in RdfWriterConverter
        String baseURI = resource.uri().toString();
        var mediaType = resource.mediaType().withoutParameters().toString();
        var writerFormat = Rio.getWriterFormatForMIMEType(mediaType);
        if (writerFormat.isEmpty()) writerFormat = Rio.getWriterFormatForFileName(baseURI);
        if (writerFormat.isEmpty()) return Optional.empty();

        var ioWriter = resource.charSink().openBufferedStream();
        var rdfWriter = Rio.createWriter(writerFormat.get(), ioWriter, baseURI);
        var writerConfig = new WriterConfig();
        rdfWriter.setWriterConfig(writerConfig);
        return Optional.of(new WritableResourceRDFHandler(rdfWriter, ioWriter));
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
        ioWriter.close();
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        rdfWriter.startRDF();
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        rdfWriter.endRDF();
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
