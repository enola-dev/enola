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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.Literal;
import dev.enola.thing.Thing.Builder;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;

import java.io.IOException;

public class ProtoThingIntoJavaThingBuilderConverter
        implements ConverterInto<dev.enola.thing.proto.Thing, dev.enola.thing.Thing.Builder> {

    private final DatatypeRepository datatypeRepository;

    public ProtoThingIntoJavaThingBuilderConverter(DatatypeRepository datatypeRepository) {
        this.datatypeRepository = datatypeRepository;
    }

    @Override
    public boolean convertInto(Thing from, Builder into) throws ConversionException, IOException {
        into.iri(from.getIri());
        for (var entry : from.getFieldsMap().entrySet()) {
            var iri = entry.getKey();
            var value = entry.getValue();
            into.set(iri, object(value));
        }
        return true;
    }

    private Object object(Value protoThingValue) {
        switch (protoThingValue.getKindCase()) {
            case STRING:
                return protoThingValue.getString();
            case LINK:
                return protoThingValue.getLink();
            case LITERAL:
                {
                    var datatypeValue = protoThingValue.getLiteral().getValue();
                    var datatypeIRI = protoThingValue.getLiteral().getDatatype();
                    var datatype = datatypeRepository.get(datatypeIRI);
                    if (datatype != null)
                        return datatype.stringConverter().convertFrom(datatypeValue);
                    else return new Literal(datatypeValue, datatypeIRI);
                }
            case LIST:
                {
                    var listProto = protoThingValue.getList();
                    var size = listProto.getValuesCount();
                    var listBuilder = ImmutableList.builderWithExpectedSize(size);
                    for (var protoListValue : listProto.getValuesList()) {
                        listBuilder.add(object(protoListValue));
                    }
                    return listBuilder.build();
                }
            case STRUCT:
                {
                    // Nota Bene: We ignore "inner" IRIs of proto Thing, because it should be empty!
                    var protoStruct = protoThingValue.getStruct();
                    var mapSize = protoStruct.getFieldsMap().size();
                    var mapBuilder = ImmutableMap.builderWithExpectedSize(mapSize);
                    for (var entry : protoStruct.getFieldsMap().entrySet()) {
                        var iri = entry.getKey();
                        var value = entry.getValue();
                        mapBuilder.put(iri, object(value));
                    }
                    return mapBuilder.build();
                }
            default:
                throw new IllegalArgumentException(protoThingValue.getKindCase().name());
        }
    }
}
