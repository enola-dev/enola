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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;

import static dev.enola.common.function.MoreStreams.forEach;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.rdf.RdfReaderConverter;
import dev.enola.rdf.RdfThingConverter;
import dev.enola.thing.ImmutableThing;
import dev.enola.thing.proto.Thing;

import org.junit.Test;

import java.io.IOException;

/**
 * Tests for {@link JavaThingToProtoThingConverter} and {@link ThingAdapter} and {@link
 * ProtoThingIntoJavaThingBuilderConverter}.
 */
public class ThingConvertersTest {

    private final DatatypeRepository datatypeRepo = new DatatypeRepositoryBuilder().build();

    @Test
    public void picasso() throws Exception {
        var cpr = new ClasspathResource("picasso.ttl");
        var rdf4jModel = new RdfReaderConverter().convert(cpr).get();
        var inProtoThingStream = new RdfThingConverter().convert(rdf4jModel);
        forEach(inProtoThingStream, inProtoThing -> check(inProtoThing.build()));
    }

    private void check(Thing inProtoThing) throws ConversionException, IOException {
        var javaThing = new ThingAdapter(inProtoThing, datatypeRepo);
        var outProtoThing = new JavaThingToProtoThingConverter().convert(javaThing).build();
        assertThat(outProtoThing).isEqualTo(inProtoThing);

        var pt2jtC = new ProtoThingIntoJavaThingBuilderConverter(datatypeRepo);
        var javaThingBuilder = ImmutableThing.builder();
        assertThat(pt2jtC.convertInto(inProtoThing, javaThingBuilder)).isTrue();
        outProtoThing =
                new JavaThingToProtoThingConverter().convert(javaThingBuilder.build()).build();
        assertThat(outProtoThing).isEqualTo(inProtoThing);
    }
}
