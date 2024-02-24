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
import dev.enola.thing.Thing;
import dev.enola.thing.Thing.Builder;
import dev.enola.thing.ThingOrBuilder;
import dev.enola.thing.Value;
import dev.enola.thing.Value.Link;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.base.CoreDatatype;

class RdfThingConverter
        implements Converter<Model, ThingOrBuilder>, ConverterInto<Model, Thing.Builder> {

    @Override
    public Thing convert(Model input) throws ConversionException {
        if (input.isEmpty()) {
            return Thing.getDefaultInstance();
        }

        var thing = Thing.newBuilder();
        convertInto(input, thing);
        return thing.build();
    }

    @Override
    public boolean convertInto(Model input, Builder thing) throws ConversionException {
        var subjects = input.subjects();
        if (subjects.size() > 1) {
            throw new ConversionException(
                    "Cannot convert RDF with >1 statements to a single Thing");
        }

        var subject = subjects.iterator().next();
        if (subject.isIRI()) {
            var subjectAsIRI = (IRI) subject;
            thing.setIri(subjectAsIRI.stringValue());
        }

        var statementIterator = input.iterator();
        while (statementIterator.hasNext()) {
            var statement = statementIterator.next();
            thing.putFields(statement.getPredicate().stringValue(), convert(statement.getObject()));
        }

        return true;
    }

    private Value convert(org.eclipse.rdf4j.model.Value rdfValue) {
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

        } else if (rdfValue.isResource()) {
            throw new UnsupportedOperationException("TODO");

        } else if (rdfValue.isTriple()) {
            throw new UnsupportedOperationException("TODO");

        } else if (rdfValue.isBNode()) {
            throw new UnsupportedOperationException("TODO");
        }

        return value.build();
    }
}
