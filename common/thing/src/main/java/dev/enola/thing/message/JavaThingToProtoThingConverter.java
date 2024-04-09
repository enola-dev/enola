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
import dev.enola.thing.Link;

import java.net.URI;

public class JavaThingToProtoThingConverter
        implements Converter<dev.enola.thing.Thing, dev.enola.thing.proto.Thing.Builder> {

    // TODO private final DatatypeRepository datatypeRepository;

    @Override
    public dev.enola.thing.proto.Thing.Builder convert(dev.enola.thing.Thing input)
            throws ConversionException {
        var protoBuilder = dev.enola.thing.proto.Thing.newBuilder();
        protoBuilder.setIri(input.iri());
        for (var property : input.properties().entrySet()) {
            var protoValue = dev.enola.thing.proto.Value.newBuilder();
            var iri = property.getKey();
            var object = property.getValue();
            switch (object) {
                case Link link:
                    protoValue.setLink(link.iri());
                    break;

                case URI uri:
                    protoValue.setLink(uri.toString());
                    break;

                case String string:
                    protoValue.setString(string);
                    break;

                default:
                    throw new IllegalStateException(
                            "TODO: Implement support for: " + object.getClass() + " :: " + object);
            }
            protoBuilder.putFields(iri, protoValue.build());
        }
        return protoBuilder;
    }
}
