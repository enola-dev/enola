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
package dev.enola.rdf.proto;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Thing.Builder;
import dev.enola.thing.proto.Value;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.base.CoreDatatype;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Converts RDF4j {@link Model} into Proto {@link Thing}s.
 *
 * <p>See {@link ProtoThingRdfConverter} for the "opposite" of this.
 */
public class RdfProtoThingsConverter implements Converter<Model, Stream<Thing.Builder>> {

    // TODO In general, an RDF stream of statements is not "ordered"; there could be "later updates"
    // to "previous things" at any time. The design of this current default implementation takes
    // this into account - which makes this very inefficient from a memory usage point of view
    // for very large RDF graphs. Future optimizations could include (optional, always) modes
    // which either makes assumptions when "a Thing is completed" (perhaps upon encountering
    // the first a non-Blank statement for another Subject?), or which uses
    // for (var subject : model.subjects()) filter(subject, null, null) ...
    // to do this in a more orderly fashion - if that scales better?
    // (Perhaps keep such different "strategies" configurable?)

    public List<Thing.Builder> convertToList(Model input) throws ConversionException {
        return convert(input).toList();
    }

    record Pair(Map<String, Thing.Builder> structs, Map<String, BNode> collectionsFirsts) {}

    @Override
    public Stream<Thing.Builder> convert(Model input) {
        // These are the "root" Things; i.e. the Statements where Subject is an IRI.
        final Map<IRI, Thing.Builder> roots = new HashMap<>();

        // These are the "contained" Things, which are inside "struct" of another Thing; i.e. the
        // Statements where the Subject is a BNode with an IRI (which is the key of this
        // Map).
        Map<String, Thing.Builder> structs = new HashMap<>();

        // These are the RDF Collections; the key is again a BNode IRI
        Map<String, BNode> collectionsFirsts = new HashMap<>();

        Pair pair = new Pair(structs, collectionsFirsts);

        List<Consumer<Pair>> deferred = new ArrayList<>();

        for (var statement : input) {
            Thing.Builder thing;
            var predicate = statement.getPredicate();
            var subject = statement.getSubject();
            if (subject.isIRI()) {
                var subjectIRI = (IRI) subject;
                thing =
                        roots.computeIfAbsent(
                                subjectIRI, iri -> Thing.newBuilder().setIri(iri.stringValue()));

            } else if (subject.isBNode()) {
                var subjectBNode = (BNode) subject;
                var subjectBNodeID = subjectBNode.getID();
                if (predicate.equals(RDF.FIRST)) {
                    collectionsFirsts.put(subjectBNodeID, subjectBNode);
                    continue;
                } else if (predicate.equals(RDF.REST)) {
                    // Ignore, because the use of RDFCollections.asValues() handles this, see above.
                    continue;
                }
                thing = structs.computeIfAbsent(subjectBNodeID, id -> Thing.newBuilder());

            } else throw new UnsupportedOperationException("TODO: " + subject);

            // The goal of this is to turn an RDF Object List into a Thing List Value
            var statements = input.filter(subject, predicate, null);
            if (statements.size() == 1) {
                var value = convert(input, thing, predicate, statement.getObject(), deferred);
                thing.putProperties(predicate.stringValue(), value.build());
            } else {
                // TODO Should distinguish List vs Set with 'ordered' in Thing.proto ...
                var protoValueList = dev.enola.thing.proto.Value.List.newBuilder();
                for (var subStatement : statements) {
                    var object = subStatement.getObject();
                    var protoValue = convert(input, thing, predicate, object, deferred);
                    protoValueList.addValues(protoValue);
                }
                var value = Value.newBuilder().setList(protoValueList);
                thing.putProperties(statement.getPredicate().stringValue(), value.build());
            }
        }

        for (var action : deferred) {
            action.accept(pair);
        }

        return roots.values().stream();
    }

    private static dev.enola.thing.proto.Value.Builder convert(
            Model model,
            Builder thing,
            IRI predicate,
            org.eclipse.rdf4j.model.Value rdfValue,
            List<Consumer<Pair>> deferred) {
        var value = Value.newBuilder();
        if (rdfValue.isIRI()) {
            value.setLink(rdfValue.stringValue());

        } else if (rdfValue.isLiteral()) {
            var rdfLiteral = (org.eclipse.rdf4j.model.Literal) rdfValue;
            var optLang = rdfLiteral.getLanguage();
            var datatype = rdfLiteral.getDatatype();
            if (CoreDatatype.XSD.STRING.getIri().equals(datatype)) {
                value.setString(rdfValue.stringValue());

            } else if (optLang.isPresent()) {
                var langString = dev.enola.thing.proto.Value.LangString.newBuilder();
                langString.setText(rdfLiteral.stringValue());
                langString.setLang(optLang.get());
                value.setLangString(langString);

            } else {
                var literal = dev.enola.thing.proto.Value.Literal.newBuilder();
                literal.setDatatype(rdfLiteral.getDatatype().stringValue());
                literal.setValue(rdfLiteral.stringValue());
                value.setLiteral(literal);
            }

        } else if (rdfValue.isBNode()) {
            var bNodeID = ((BNode) rdfValue).getID();
            deferred.add(
                    pair -> {
                        var containedThing = pair.structs.get(bNodeID);
                        if (containedThing != null) {
                            value.setStruct(containedThing);

                        } else {
                            var collectionStatement = pair.collectionsFirsts.get(bNodeID);
                            if (collectionStatement == null)
                                throw new IllegalStateException(
                                        bNodeID
                                                + " not found, neither in structs nor in"
                                                + " collectionsFirsts");

                            var rdfValueList = new ArrayList<org.eclipse.rdf4j.model.Value>(0);
                            RDFCollections.asValues(model, collectionStatement, rdfValueList);
                            var protoValueList = dev.enola.thing.proto.Value.List.newBuilder();
                            protoValueList.setOrdered(true);

                            for (var rdf4jValue : rdfValueList) {
                                var protoValue =
                                        convert(model, thing, predicate, rdf4jValue, deferred);
                                protoValueList.addValues(protoValue);
                            }
                            value.setList(protoValueList);
                        }
                        thing.putProperties(predicate.stringValue(), value.build());
                    });

        } else if (rdfValue.isTriple()) {
            throw new UnsupportedOperationException("TODO: Triple");

        } else if (rdfValue.isResource()) {
            throw new UnsupportedOperationException("TODO: Resource");
        }

        return value;
    }
}
