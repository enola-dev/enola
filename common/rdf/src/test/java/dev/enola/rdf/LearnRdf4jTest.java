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

import static com.google.common.truth.Truth.assertThat;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.LOCN;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Test;

public class LearnRdf4jTest {

    // TODO Ontology to define what's "valid" for http://www.w3.org/ns/locn#location?

    // https://rdf4j.org/documentation/tutorials/getting-started/

    Model picasso1() {
        // Namespace
        // String ex = "http://example.enola.dev/";
        var ex = Values.namespace("ex", "http://example.enola.dev/");

        // Create IRIs for the resources we want to add.
        IRI picasso = Values.iri(ex, "Picasso");
        IRI artist = Values.iri(ex, "Artist");

        // Create a new, empty Model object.
        Model model = new TreeModel();

        // Add our first statement: Picasso is an Artist
        model.add(picasso, RDF.TYPE, artist);

        // Second statement: Picasso's first name is "Pablo".
        model.add(picasso, FOAF.FIRST_NAME, Values.literal("Pablo"));

        // Terzo
        model.add(picasso, LOCN.LOCATION, Values.literal("Spain", "en"));
        // TODO model.add(picasso, LOCN.LOCATION, Values.literal("España", "es"));

        BNode address = Values.bnode("f034741e5da3451ead5b5972d6cf75311");
        model.add(picasso, Values.iri(ex, "homeAddress"), address);
        model.add(address, Values.iri(ex, "street"), Values.literal("31 Art Gallery"));
        model.add(address, Values.iri(ex, "city"), Values.literal("Barcelona"));

        IRI dali = Values.iri(ex, "Dalí");
        model.add(dali, RDF.TYPE, artist);
        model.add(dali, FOAF.FIRST_NAME, Values.literal("Salvador"));

        return model;
    }

    Model picasso2() {
        BNode address = Values.bnode("f034741e5da3451ead5b5972d6cf75311");
        return new ModelBuilder()
                .setNamespace("ex", "http://example.enola.dev/")
                .setNamespace(FOAF.NS)
                .subject("ex:Picasso")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.FIRST_NAME, "Pablo")
                .add(LOCN.LOCATION, Values.literal("Spain", "en"))
                // TODO .add(LOCN.LOCATION, Values.literal("España", "es"))
                .add("ex:homeAddress", address) // link the blank node
                .subject(address) // switch the subject
                .add("ex:street", "31 Art Gallery")
                .add("ex:city", "Barcelona")
                .subject("ex:Dalí")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.FIRST_NAME, "Salvador")
                .build();
    }

    @Test
    public void testRDF() throws Exception {
        var picasso1 = picasso1();
        var picasso2 = picasso2();
        assertThat(picasso1).isEqualTo(picasso2);
    }
}
