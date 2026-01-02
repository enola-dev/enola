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

import com.google.common.collect.ComparisonChain;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.WritableResource;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.DynamicModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;

import java.util.Comparator;

// TODO https://github.com/enola-dev/enola/issues/1103 : Replace this with "real" (full) RDF-Canon.
public class RdfCanonicalizer {

    // NB: This *DOES* also sort any internal blank statements!
    // (Because it operates on ["flat"] RDF4j Statement - NOT the ["nested"] Thing API.)

    private static final Model EMPTY_MODEL = new DynamicModel(new LinkedHashModelFactory());

    private static final Comparator<? super Statement> COMPARATOR =
            new Comparator<Statement>() {
                @Override
                public int compare(Statement o1, Statement o2) {
                    return ComparisonChain.start()
                            .compare(o1.getSubject().stringValue(), o2.getSubject().stringValue())
                            .compare(
                                    o1.getPredicate().stringValue(),
                                    o2.getPredicate().stringValue())
                            .compare(o1.getObject().stringValue(), o2.getObject().stringValue())
                            .result();
                }
            };

    private final RdfReaderConverter rdfReaderConverter;
    private final RdfWriterConverter rdfWriterConverter;

    public RdfCanonicalizer(ResourceProvider rp) {
        rdfReaderConverter = new RdfReaderConverter(rp);
        rdfWriterConverter = new RdfWriterConverter();
    }

    public Model orderStatements(Model modelIN) {
        var modelOUT = new DynamicModel(new LinkedHashModelFactory());
        var namespaces = modelIN.getNamespaces();
        for (var namespace : namespaces) modelOUT.setNamespace(namespace);

        // Sort statements first by Resource IRI, then by their predicate IRI
        modelIN.stream().sorted(COMPARATOR).forEach(modelOUT::add);

        return modelOUT;
    }

    public void canonicalize(ReadableResource in, WritableResource out) {
        var model = rdfReaderConverter.convert(in).orElse(EMPTY_MODEL);
        var canonicalModel = orderStatements(model);
        rdfWriterConverter.convertIntoOrThrow(canonicalModel, out);
    }
}
