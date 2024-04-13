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
import dev.enola.thing.LangString;
import dev.enola.thing.Link;
import dev.enola.thing.proto.Value;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JavaThingToProtoThingConverter
        implements Converter<dev.enola.thing.Thing, dev.enola.thing.proto.Thing.Builder> {

    // TODO private final DatatypeRepository datatypeRepository;

    @Override
    public dev.enola.thing.proto.Thing.Builder convert(dev.enola.thing.Thing javaThing)
            throws ConversionException {
        var protoBuilder = dev.enola.thing.proto.Thing.newBuilder();
        protoBuilder.setIri(javaThing.iri());
        map2proto(protoBuilder, javaThing.properties().entrySet());
        return protoBuilder;
    }

    private void map2proto(
            dev.enola.thing.proto.Thing.Builder protoBuilder,
            Iterable<Entry<String, Object>> properties) {
        for (var property : properties) {
            var iri = property.getKey();
            var object = property.getValue();
            protoBuilder.putFields(iri, toValue(object));
        }
    }

    private Value toValue(Object object) {
        var protoValue = dev.enola.thing.proto.Value.newBuilder();
        switch (object) {
            case String string:
                protoValue.setString(string);
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
                    protoList.addValues(toValue(e));
                }
                protoValue.setList(protoList);
                break;

            case Map<?, ?> map:
                var propertiesMap = (Map<String, Object>) map;
                var protoThing = dev.enola.thing.proto.Thing.newBuilder();
                map2proto(protoThing, propertiesMap.entrySet());
                protoValue.setStruct(protoThing);
                break;

            case LangString langString:
                var protoLangString = dev.enola.thing.proto.Value.LangString.newBuilder();
                protoLangString.setText(langString.text());
                protoLangString.setLang(langString.lang());
                protoValue.setLangString(protoLangString);
                break;

            default:
                // TODO Consult the datatypeRepository ... -- WITH TEST COVERAGE IN picasso.ttl
                throw new IllegalStateException(
                        "TODO: Implement support for: " + object.getClass() + " :: " + object);
        }
        return protoValue.build();
    }
}
