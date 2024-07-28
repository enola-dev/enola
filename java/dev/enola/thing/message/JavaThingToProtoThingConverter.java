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
package dev.enola.thing.message;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.LangString;
import dev.enola.thing.Link;
import dev.enola.thing.Literal;
import dev.enola.thing.proto.Value;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JavaThingToProtoThingConverter
        implements Converter<dev.enola.thing.Thing, dev.enola.thing.proto.Thing.Builder> {

    private final DatatypeRepository datatypeRepository;

    public JavaThingToProtoThingConverter(DatatypeRepository datatypeRepository) {
        this.datatypeRepository = datatypeRepository;
    }

    @Override
    public dev.enola.thing.proto.Thing.Builder convert(dev.enola.thing.Thing javaThing)
            throws ConversionException {
        var protoBuilder = dev.enola.thing.proto.Thing.newBuilder();
        protoBuilder.setIri(javaThing.iri());
        map2proto(protoBuilder, javaThing.properties().entrySet(), javaThing);
        return protoBuilder;
    }

    private void map2proto(
            dev.enola.thing.proto.Thing.Builder protoBuilder,
            Iterable<Entry<String, Object>> properties,
            dev.enola.thing.Thing javaThing) {
        for (var property : properties) {
            var iri = property.getKey();
            var object = property.getValue();
            protoBuilder.putFields(iri, toValue(object, iri, javaThing));
        }
    }

    private Value toValue(Object object, String propertyIRI, dev.enola.thing.Thing javaThing) {
        var protoValue = dev.enola.thing.proto.Value.newBuilder();
        // javaThing == null when it's for a Map (from Blank Node)
        // TODO remove null check once fully switching to PredicatesObjects instead Map
        var datatypeIRI = javaThing != null ? javaThing.datatype(propertyIRI) : null;
        switch (object) {
            case String string:
                if (datatypeIRI == null) protoValue.setString(string);
                else protoValue.setLiteral(toProtoLiteral(string, datatypeIRI));
                break;

            case Link link:
                protoValue.setLink(link.iri());
                break;

            case URI uri:
                protoValue.setLink(uri.toString());
                break;

            case List<?> list:
                var protoList = dev.enola.thing.proto.Value.List.newBuilder();
                for (var e : list) {
                    // TODO This assumes the List is heterogenous... which it may not be! Test...
                    protoList.addValues(toValue(e, propertyIRI, javaThing));
                }
                protoValue.setList(protoList);
                break;

            case Map<?, ?> map: // TODO Replace Map with PredicatesObjects, see below
                var propertiesMap = (Map<String, Object>) map;
                var protoThing = dev.enola.thing.proto.Thing.newBuilder();
                // TODO Fix NULL... Must be a PredicatesObjects instead of Map eventually
                map2proto(protoThing, propertiesMap.entrySet(), null);
                protoValue.setStruct(protoThing);
                break;

            case LangString langString:
                var protoLangString = dev.enola.thing.proto.Value.LangString.newBuilder();
                protoLangString.setText(langString.text());
                protoLangString.setLang(langString.lang());
                protoValue.setLangString(protoLangString);
                break;

            case Literal literal:
                protoValue.setLiteral(toProtoLiteral(literal.value(), literal.datatypeIRI()));
                break;

            default:
                var datatype = datatypeRepository.get(datatypeIRI);
                if (datatype == null)
                    throw new IllegalStateException(
                            "TODO: Implement support for: "
                                    + datatypeIRI
                                    + " ("
                                    + object.getClass()
                                    + ") :: "
                                    + object
                                    + " in "
                                    + datatypeRepository.list());
                String valueAsString = datatype.stringConverterFromObject().convertTo(object);
                protoValue.setLiteral(toProtoLiteral(valueAsString, datatype.iri()));
        }
        return protoValue.build();
    }

    private static dev.enola.thing.proto.Value.Literal toProtoLiteral(
            String value, String datatypeIRI) {
        var protoLiteral = dev.enola.thing.proto.Value.Literal.newBuilder();
        protoLiteral.setValue(value);
        protoLiteral.setDatatype(datatypeIRI);
        return protoLiteral.build();
    }
}
