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

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.common.io.resource.WritableResource;

import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.NamespaceAware;
import org.eclipse.rdf4j.model.Statement;

import java.io.IOException;

/**
 * Writes RDFJ4j {@link Statement}s (like {@link org.eclipse.rdf4j.model.Model}) into a {@link
 * WritableResource}.
 */
public class RdfWriterConverter implements ConverterInto<Iterable<Statement>, WritableResource> {

    @Override
    public boolean convertInto(Iterable<Statement> from, WritableResource into)
            throws ConversionException {

        try {
            var opt = WritableResourceRDFHandler.create(into);
            if (opt.isEmpty()) return false;

            try (var closeableRDFHandler = opt.get()) {
                if (from instanceof NamespaceAware) {
                    for (Namespace ns : ((NamespaceAware) from).getNamespaces()) {
                        closeableRDFHandler.handleNamespace(ns.getPrefix(), ns.getName());
                    }
                }
                for (var statement : from) closeableRDFHandler.handleStatement(statement);

                return true;
            }

        } catch (IOException e) {
            throw new ConversionException("WritableResourceRDFHandler.create failed: " + into, e);
        }
    }
}
