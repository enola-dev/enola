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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.function.MoreStreams.forEach;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.model.schemaorg.Datatypes;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.rdf.io.RdfReaderConverter;
import dev.enola.rdf.proto.RdfProtoThingsConverter;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.proto.Thing;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Tests for {@link JavaThingToProtoThingConverter} and {@link ThingAdapter} and {@link
 * ProtoThingIntoJavaThingBuilderConverter}.
 */
public class ThingConvertersTest {

    @Rule public SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    private final DatatypeRepository datatypeRepo =
            new DatatypeRepositoryBuilder().store(Datatypes.DATE).build();

    @Test
    public void picasso() throws IOException {
        var cpr = new ClasspathResource("picasso.ttl");
        var rdf4jModel = new RdfReaderConverter(iri -> null).convert(cpr).get();
        var inProtoThingStream = new RdfProtoThingsConverter().convert(rdf4jModel);
        forEach(inProtoThingStream, inProtoThing -> check(inProtoThing.build()));
    }

    private void check(Thing inProtoThing) throws ConversionException, IOException {
        dev.enola.thing.Thing javaThing = new ThingAdapter(inProtoThing, datatypeRepo);
        checkDateDatatype(javaThing);
        var outProtoThing =
                new JavaThingToProtoThingConverter(datatypeRepo).convert(javaThing).build();
        assertThat(outProtoThing).isEqualTo(inProtoThing);

        var pt2jtC = new ProtoThingIntoJavaThingBuilderConverter(datatypeRepo);
        var javaThingBuilder = ImmutableThing.builder();
        assertThat(pt2jtC.convertInto(inProtoThing, javaThingBuilder)).isTrue();
        javaThing = javaThingBuilder.build();
        checkDateDatatype(javaThing);
        outProtoThing = new JavaThingToProtoThingConverter(datatypeRepo).convert(javaThing).build();
        assertThat(outProtoThing).isEqualTo(inProtoThing);
    }

    private void checkDateDatatype(dev.enola.thing.Thing javaThing) {
        if ("http://example.enola.dev/Dalí".equals(javaThing.iri())) {
            LocalDate birthDate = javaThing.get("https://schema.org/birthDate", LocalDate.class);
            assertThat(birthDate).isNotNull();
        }
    }
}
