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

import com.google.common.base.Strings;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.common.convert.ConverterInto;
import dev.enola.thing.Thing;
import dev.enola.thing.ThingOrBuilder;
import dev.enola.thing.Value.LangString;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.ValidatingValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.util.HashMap;
import java.util.Map;

class ThingRdfConverter
        implements Converter<ThingOrBuilder, Model>, ConverterInto<ThingOrBuilder, RDFHandler> {

    private final ValueFactory vf;

    public ThingRdfConverter(ValueFactory vf) {
        this.vf = vf;
    }

    public ThingRdfConverter() {
        // just like in Values.VALUE_FACTORY
        this(new ValidatingValueFactory(SimpleValueFactory.getInstance()));
    }

    @Override
    public Model convert(ThingOrBuilder input) throws ConversionException {
        var model = new ModelBuilder().build();
        var statementCollector = new StatementCollector(model);
        convertInto(input, statementCollector);
        return model;
    }

    @Override
    public boolean convertInto(ThingOrBuilder from, RDFHandler into) throws ConversionException {
        into.startRDF();
        Map<BNode, Thing> containedThings = new HashMap<>();
        convertInto(null, from, into, containedThings);
        into.endRDF();
        return true;
    }

    private void convertInto(
            String bNodeID, ThingOrBuilder from, RDFHandler into, Map<BNode, Thing> containedThings)
            throws ConversionException {
        Resource subject;
        try {
            var iri = from.getIri();
            if (!Strings.isNullOrEmpty(iri)) {
                subject = vf.createIRI(iri);
            } else if (!Strings.isNullOrEmpty(bNodeID)) {
                subject = vf.createBNode(bNodeID);
            } else {
                throw new IllegalStateException(from.toString());
            }
        } catch (IllegalArgumentException e) {
            // https://github.com/eclipse-rdf4j/rdf4j/pull/4919
            throw new IllegalArgumentException(from.getIri(), e);
        }
        for (var field : from.getFieldsMap().entrySet()) {
            IRI predicate = vf.createIRI(field.getKey());
            var object = convert(field.getValue(), containedThings);
            Statement statement = vf.createStatement(subject, predicate, object);
            into.handleStatement(statement);
        }

        for (var containedThing : containedThings.entrySet()) {
            Map<BNode, Thing> deeperContainedThings = new HashMap<>();
            convertInto(
                    containedThing.getKey().getID(),
                    containedThing.getValue(),
                    into,
                    deeperContainedThings);
        }
    }

    private org.eclipse.rdf4j.model.Value convert(
            dev.enola.thing.Value value, Map<BNode, Thing> containedThings) {
        return switch (value.getKindCase()) {
            case LINK -> vf.createIRI(value.getLink().getIri());

            case STRING -> vf.createLiteral(value.getString());

            case LITERAL -> {
                var literal = value.getLiteral();
                yield vf.createLiteral(literal.getValue(), vf.createIRI(literal.getDatatype()));
            }

            case LANG_STRING -> {
                LangString langString = value.getLangString();
                yield vf.createLiteral(langString.getText(), langString.getLang());
            }

            case STRUCT -> {
                BNode bNode;
                var containedThing = value.getStruct();
                var containedThingIRI = containedThing.getIri();
                if (!Strings.isNullOrEmpty(containedThingIRI)) {
                    bNode = vf.createBNode(containedThingIRI);
                } else {
                    bNode = vf.createBNode();
                }
                containedThings.put(bNode, containedThing);
                yield bNode;
            }

                // case LIST -> throw new UnsupportedOperationException("TODO");

            case KIND_NOT_SET -> throw new IllegalArgumentException(value.toString());
        };
    }
}
