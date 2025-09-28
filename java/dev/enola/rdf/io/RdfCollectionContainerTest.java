/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.truth.extensions.proto.ProtoTruth;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.StringResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.rdf.proto.RdfProtoThingsConverter;
import dev.enola.thing.Link;
import dev.enola.thing.message.JavaThingToProtoThingConverter;
import dev.enola.thing.message.ThingAdapter;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

/**
 * Test RDF4j Collections &amp; Containers I/O conversion to Proto Thing and Java Thing API.
 *
 * <p>See:
 *
 * <ol>
 *   <li><a href="https://rdf4j.org/documentation/programming/model/#rdf-collections">RDF4j API
 *       documentation</a>
 *   <li><a href="https://www.w3.org/TR/rdf-schema/#ch_containervocab">RDFS Spec</a>
 *   <li><a href="https://www.w3.org/TR/turtle/#collections">Turtle Spec</a>
 * </ol>
 */
public class RdfCollectionContainerTest {

    // TODO When ThingsRdfConverter is implemented, also test that here, with RdfWriterConverter

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    String rdf = "@prefix : <http://example.org/>. :thing :property ( :thing1 :thing2 ).";

    String thingIRI = "http://example.org/thing";
    String propertyIRI = "http://example.org/property";
    String thing1IRI = "http://example.org/thing1";
    String thing2IRI = "http://example.org/thing2";

    // http://example.org/thing, http://example.org/property, _:b1
    // _:b1, http://www.w3.org/1999/02/22-rdf-syntax-ns#first, http://example.org/thing1
    // _:b1, http://www.w3.org/1999/02/22-rdf-syntax-ns#rest, _:b2
    // _:b2, http://www.w3.org/1999/02/22-rdf-syntax-ns#first, http://example.org/thing2
    // _:b2, http://www.w3.org/1999/02/22-rdf-syntax-ns#rest,
    //                                    http://www.w3.org/1999/02/22-rdf-syntax-ns#nil

    @Test
    public void npeRdfListProtoPredicatesObjectsAdapter() {
        DatatypeRepository dtr = DatatypeRepository.EMPTY;
        ResourceProvider rp = iri -> null;

        var rrc = new RdfReaderConverter(rp);
        var rdc = new RdfProtoThingsConverter();

        var resource = StringResource.of(rdf, RdfMediaTypes.TURTLE);
        var model = rrc.convert(resource).get();
        var protoThing1 = rdc.convert(model).findFirst().get().build();
        checkProtoThing(protoThing1);

        var javaThing = new ThingAdapter(protoThing1, dtr);
        checkJavaThing(javaThing);

        var jt2pt = new JavaThingToProtoThingConverter(dtr);
        checkProtoThing(jt2pt.convert(javaThing).build());
    }

    void checkJavaThing(dev.enola.thing.Thing javaThing) {
        assertThat(javaThing.predicateIRIs()).containsExactly(propertyIRI);
        var expectedList = List.of(new Link(thing1IRI), new Link(thing2IRI));
        assertThat(javaThing.properties()).containsExactly(propertyIRI, expectedList);
        assertThat((Iterable<?>) javaThing.get(propertyIRI)).isEqualTo(expectedList);
        assertThat(javaThing.get(propertyIRI, List.class)).isEqualTo(expectedList);
    }

    void checkProtoThing(Thing protoThing) {
        Thing expectedProtoThing =
                Thing.newBuilder()
                        .setIri(thingIRI)
                        .putProperties(
                                propertyIRI,
                                Value.newBuilder()
                                        .setList(
                                                Value.List.newBuilder()
                                                        .setOrdered(true)
                                                        .addValues(
                                                                Value.newBuilder()
                                                                        .setLink(thing1IRI)
                                                                        .build())
                                                        .addValues(
                                                                Value.newBuilder()
                                                                        .setLink(thing2IRI)
                                                                        .build())
                                                        .build())
                                        .build())
                        .build();
        ProtoTruth.assertThat(protoThing).isEqualTo(expectedProtoThing);
    }
}
