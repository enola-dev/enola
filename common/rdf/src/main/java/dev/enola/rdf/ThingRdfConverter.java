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
import dev.enola.common.convert.ConverterInto;
import dev.enola.thing.ThingOrBuilder;
import dev.enola.thing.Value.LangString;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.ValidatingValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

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
        IRI subject = vf.createIRI(from.getIri());

        for (var field : from.getFieldsMap().entrySet()) {
            IRI predicate = vf.createIRI(field.getKey());
            var object = convert(field.getValue());
            Statement statement = vf.createStatement(subject, predicate, object);
            into.handleStatement(statement);
        }

        into.endRDF();
        return true;
    }

    private org.eclipse.rdf4j.model.Value convert(dev.enola.thing.Value value) {
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

            case LIST -> throw new UnsupportedOperationException("TODO");

            case STRUCT -> throw new UnsupportedOperationException("TODO");

            case KIND_NOT_SET -> throw new IllegalArgumentException(value.toString());
        };
    }
}
