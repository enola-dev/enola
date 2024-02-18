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

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;

import com.google.common.truth.extensions.proto.ProtoTruth;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.thing.Thing;

import org.junit.Test;

import java.io.IOException;

public class RdfThingConverterTest {

    private final ProtoIO proto = new ProtoIO();
    private final RdfThingConverter c = new RdfThingConverter();

    @Test
    public void rdfToThing() throws ConversionException, IOException {
        var model =
                new RdfReaderConverter()
                        .convert(new ClasspathResource("picasso.turtle", RdfMediaType.TURTLE));
        var actual = c.convertFrom(model);

        var expected =
                proto.read(
                        new ClasspathResource("picasso.thing.yaml", YAML_UTF_8),
                        Thing.newBuilder(),
                        Thing.class);
        ProtoTruth.assertThat(actual).isEqualTo(expected);

        // TODO ...
    }

    @Test
    public void thingToRDF() {}

    @Test
    public void messageToRDF() {}
}
