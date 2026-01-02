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
package dev.enola.rdf.io;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.truth.Truth;
import com.google.common.truth.extensions.proto.ProtoTruth;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.protobuf.test.TestComplex;
import dev.enola.protobuf.test.TestSimple;
import dev.enola.rdf.proto.ProtoThingRdfConverter;
import dev.enola.rdf.proto.RdfProtoThingsConverter;
import dev.enola.thing.io.ThingMediaTypes;
import dev.enola.thing.message.MessageToThingConverter;
import dev.enola.thing.message.MessageWithIRI;
import dev.enola.thing.proto.Thing;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Values;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class RdfThingConverterTest {

    public @Rule SingletonRule r =
            $(MediaTypeProviders.set(new RdfMediaTypes(), new ThingMediaTypes()));

    private final ReadableResource turtle = new ClasspathResource("picasso.ttl");

    private final ReadableResource picassoYaml = new ClasspathResource("picasso.thing.yaml");

    private final ReadableResource daliYaml = new ClasspathResource("dali.thing.yaml");

    private final ProtoIO protoReader = new ProtoIO();
    private final RdfReaderConverter rdfReader = new RdfReaderConverter(iri -> null);
    private final RdfProtoThingsConverter rdfToThingConverter = new RdfProtoThingsConverter();
    private final ProtoThingRdfConverter thingToRdfConverter = new ProtoThingRdfConverter();

    private Model rdf;
    private Thing picassoThing;
    private Thing daliThing;

    @Before
    public void before() throws ConversionException, IOException {
        rdf = rdfReader.convert(turtle).get();
        picassoThing = protoReader.read(picassoYaml, Thing.newBuilder(), Thing.class);
        daliThing = protoReader.read(daliYaml, Thing.newBuilder(), Thing.class);
    }

    @Test
    public void rdfToPicassoThing() throws ConversionException, IOException {
        var actualThings = rdfToThingConverter.convertToList(rdf);
        var expectedThing = picassoThing;
        ProtoTruth.assertThat(actualThings.get(1).build()).isEqualTo(expectedThing);
    }

    @Test
    public void rdfToDaliThing() throws ConversionException, IOException {
        var actualThings = rdfToThingConverter.convertToList(rdf);
        var expectedThing = daliThing;
        ProtoTruth.assertThat(actualThings.get(0).build()).isEqualTo(expectedThing);
    }

    @Test
    public void picassoThingToRDF() throws ConversionException {
        var actualRDF = thingToRdfConverter.convert(picassoThing);
        Truth.assertThat(rdf.remove(Values.iri("http://example.enola.dev/Dalí"), null, null))
                .isTrue();
        var expectedRDF = rdf;
        ModelSubject.assertThat(actualRDF).isEqualTo(expectedRDF);
    }

    @Test
    public void daliThingToRDF() throws ConversionException {
        var actualRDF = thingToRdfConverter.convert(daliThing);
        var expectedRDF = rdf.filter(Values.iri("http://example.enola.dev/Dalí"), null, null);
        ModelSubject.assertThat(actualRDF).isEqualTo(expectedRDF);
    }

    @Test
    public void protoMessageToRDF() throws ConversionException {
        var simple = TestSimple.newBuilder().setText("hello").setNumber(123);
        var complex =
                TestComplex.newBuilder().setSimple(simple).addSimples(simple).addSimples(simple);
        var converter = new MessageToThingConverter();
        var thing = converter.convert(new MessageWithIRI("http://test/thing", complex.build()));
        var actualRDF = thingToRdfConverter.convert(thing);
        var expectedRDF = rdfReader.convert(new ClasspathResource("proto.ttl")).get();
        ModelSubject.assertThat(actualRDF).isEqualTo(expectedRDF);
    }
}
