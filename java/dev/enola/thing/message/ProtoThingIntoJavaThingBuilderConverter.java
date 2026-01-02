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
package dev.enola.thing.message;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.LangString;
import dev.enola.thing.Link;
import dev.enola.thing.Literal;
import dev.enola.thing.Thing.Builder;
import dev.enola.thing.proto.ThingOrBuilder;
import dev.enola.thing.proto.Value;

import java.io.IOException;

/**
 * Converter of proto Thing into Java Thing Builder.
 *
 * <p>This is somewhat like {@link ThingAdapter}, but this one "converts" whereas that one only
 * "wraps".
 */
public class ProtoThingIntoJavaThingBuilderConverter
        implements ConverterInto<
                dev.enola.thing.proto.ThingOrBuilder, dev.enola.thing.Thing.Builder> {

    // TODO This is too similar to ThingAdapter, and must be merged; maybe via ThingConverterInto?

    private final DatatypeRepository datatypeRepository;

    public ProtoThingIntoJavaThingBuilderConverter(DatatypeRepository datatypeRepository) {
        this.datatypeRepository = datatypeRepository;
    }

    @Override
    @SuppressWarnings("Immutable") // TODO Remove when switching to (TBD) PredicatesObjects.Visitor
    public boolean convertInto(ThingOrBuilder from, Builder into)
            throws ConversionException, IOException {
        into.iri(from.getIri());
        for (var entry : from.getPropertiesMap().entrySet()) {
            var iri = entry.getKey();
            var value = entry.getValue();
            if (Value.KindCase.KIND_NOT_SET.equals(value.getKindCase()))
                throw new IllegalArgumentException(iri);

            if (dev.enola.thing.proto.Value.KindCase.LITERAL.equals(value.getKindCase())) {
                var datatypeValue = value.getLiteral().getValue();
                var datatypeIRI = value.getLiteral().getDatatype();
                var datatype = datatypeRepository.get(datatypeIRI);
                if (datatype != null) {
                    Object object = datatype.stringConverter().convertFrom(datatypeValue);
                    into.set(iri, object, datatypeIRI);
                } else into.set(iri, new Literal(datatypeValue, datatypeIRI));
            } else {
                into.set(iri, object(value));
            }
        }
        return true;
    }

    private Object object(Value protoThingValue) {
        switch (protoThingValue.getKindCase()) {
            case STRING:
                return protoThingValue.getString();
            case LITERAL:
                // TODO Rework things to this can use Thing.set(value, datatype) instead Literal
                var literal = protoThingValue.getLiteral();
                return new Literal(literal.getValue(), literal.getDatatype());
            case LINK:
                return new Link(protoThingValue.getLink());
            case LANG_STRING:
                var protoLangString = protoThingValue.getLangString();
                return new LangString(protoLangString.getText(), protoLangString.getLang());
            case LIST:
                {
                    var protoList = protoThingValue.getList();
                    var size = protoList.getValuesCount();

                    ImmutableCollection.Builder<Object> collectionBuilder;
                    if (protoList.getOrdered())
                        collectionBuilder = ImmutableList.builderWithExpectedSize(size);
                    else collectionBuilder = ImmutableSet.builderWithExpectedSize(size);

                    for (var protoListValue : protoList.getValuesList()) {
                        collectionBuilder.add(object(protoListValue));
                    }
                    return collectionBuilder.build();
                }
            case STRUCT:
                {
                    // Nota Bene: We ignore "inner" IRIs of proto Thing, because it should be empty!
                    var protoStruct = protoThingValue.getStruct();
                    return new PredicatesObjectsAdapter(protoStruct, datatypeRepository);
                }
            default:
                throw new IllegalArgumentException(protoThingValue.toString());
        }
    }
}
