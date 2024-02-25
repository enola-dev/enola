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
import dev.enola.thing.Thing;
import dev.enola.thing.Thing.Builder;
import dev.enola.thing.Value;
import dev.enola.thing.Value.Link;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.base.CoreDatatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

class RdfThingConverter implements Converter<Model, Stream<Thing.Builder>> {

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

    @Override
    public Stream<Thing.Builder> convert(Model input) {
        // These are the "root" Things; i.e. the Statements where Subject is an IRI.
        final Map<IRI, Thing.Builder> roots = new HashMap<>();

        // These are the "contained" Things, which are inside "struct" of another Thing; i.e. the
        // Statements where the Subject is a BNode with an ID (which is the key of this
        // Map).
        Map<String, Thing.Builder> structs = new HashMap<>();

        List<Consumer<Map<String, Thing.Builder>>> deferred = new ArrayList<>();

        for (var statement : input) {
            Thing.Builder thing;
            var subject = statement.getSubject();
            if (subject.isIRI()) {
                var subjectIRI = (IRI) subject;
                thing =
                        roots.computeIfAbsent(
                                subjectIRI, iri -> Thing.newBuilder().setIri(iri.stringValue()));

            } else if (subject.isBNode()) {
                var subjectBNode = (BNode) subject;
                var subjectBNodeID = subjectBNode.getID();
                thing = structs.computeIfAbsent(subjectBNodeID, id -> Thing.newBuilder());

            } else throw new UnsupportedOperationException("TODO: " + subject);

            // The goal of this is to turn an RDF Object List into a Thing List Value
            var predicate = statement.getPredicate();
            var statements = input.filter(subject, predicate, null);
            if (statements.size() == 1) {
                convertAndPut(thing, predicate, statement.getObject(), deferred);
                var value = convert(thing, predicate, statement.getObject(), deferred);
                thing.putFields(predicate.stringValue(), value.build());
            } else {
                var valueList = dev.enola.thing.Value.List.newBuilder();
                for (var subStatement : statements) {
                    var object = subStatement.getObject();
                    var value = convert(thing, predicate, object, deferred);
                    valueList.addValues(value);
                }
                var value = Value.newBuilder().setList(valueList);
                thing.putFields(statement.getPredicate().stringValue(), value.build());
            }
            // TODO It's surprising that this this really works (test pass) as-is?
            // What causes it not to repeat (duplicate) values?
        }

        for (var action : deferred) {
            action.accept(structs);
        }

        return roots.values().stream();
    }

    // TODO Remove this again?
    private static void convertAndPut(
            Builder thing,
            IRI predicate,
            org.eclipse.rdf4j.model.Value object,
            List<Consumer<Map<String, Builder>>> deferred) {
        var value = convert(thing, predicate, object, deferred);
        thing.putFields(predicate.stringValue(), value.build());
    }

    private static dev.enola.thing.Value.Builder convert(
            Builder thing,
            IRI predicate,
            org.eclipse.rdf4j.model.Value rdfValue,
            List<Consumer<Map<String, Builder>>> deferred) {
        var value = Value.newBuilder();
        if (rdfValue.isIRI()) {
            value.setLink(Link.newBuilder().setIri(rdfValue.stringValue()));

        } else if (rdfValue.isLiteral()) {
            var rdfLiteral = (org.eclipse.rdf4j.model.Literal) rdfValue;
            var optLang = rdfLiteral.getLanguage();
            var datatype = rdfLiteral.getDatatype();
            if (CoreDatatype.XSD.STRING.getIri().equals(datatype)) {
                value.setString(rdfValue.stringValue());

            } else if (optLang.isPresent()) {
                var langString = dev.enola.thing.Value.LangString.newBuilder();
                langString.setText(rdfLiteral.stringValue());
                langString.setLang(optLang.get());
                value.setLangString(langString);

            } else {
                var literal = dev.enola.thing.Value.Literal.newBuilder();
                literal.setDatatype(rdfLiteral.getDatatype().stringValue());
                literal.setValue(rdfLiteral.stringValue());
                value.setLiteral(literal);
            }

        } else if (rdfValue.isBNode()) {
            var bNodeID = ((BNode) rdfValue).getID();
            deferred.add(
                    map -> {
                        var containedThing = map.get(bNodeID);
                        if (containedThing == null)
                            throw new IllegalStateException(
                                    bNodeID + " not found: " + map.keySet());
                        value.setStruct(containedThing);
                        thing.putFields(predicate.stringValue(), value.build());
                    });

        } else if (rdfValue.isTriple()) {
            throw new UnsupportedOperationException("TODO: Triple");

        } else if (rdfValue.isResource()) {
            throw new UnsupportedOperationException("TODO: Resource");
        }

        return value;
    }
}
