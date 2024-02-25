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

import com.google.common.truth.Truth;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.thing.Thing;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Values;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class RdfThingConverterTest {

    private final ReadableResource turtle =
            new ClasspathResource("picasso.turtle", RdfMediaType.TURTLE);
    private final ReadableResource yaml = new ClasspathResource("picasso.thing.yaml", YAML_UTF_8);

    private final ProtoIO protoReader = new ProtoIO();
    private final RdfReaderConverter rdfReader = new RdfReaderConverter();
    private final RdfThingConverter rdfToThingConverter = new RdfThingConverter();
    private final ThingRdfConverter thingToRdfConverter = new ThingRdfConverter();

    private Model rdf;
    private Thing thing;

    @Before
    public void before() throws ConversionException, IOException {
        rdf = rdfReader.convert(turtle);
        thing = protoReader.read(yaml, Thing.newBuilder(), Thing.class);
    }

    @Test
    public void rdfToThing() throws ConversionException, IOException {
        var actualThings = rdfToThingConverter.convertToList(rdf);
        var expectedThing = thing;
        // TODO Use ProtoTruth instead of Truth (requires fixing *Builder return type)
        Truth.assertThat(actualThings.get(1)).isEqualTo(expectedThing);
    }

    @Test
    public void thingToRDF() throws ConversionException {
        var actualRDF = thingToRdfConverter.convert(thing);
        rdf.remove(Values.iri("http://example.enola.dev/Dalí"), null, null);
        var expectedRDF = rdf;
        ModelSubject.assertThat(actualRDF).isEqualTo(expectedRDF);
    }

    @Test
    public void messageToRDF() {
        // TODO Implement messageToRDF(), via MessageToThingConverter
    }
}
