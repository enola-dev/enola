/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.LOCN;
import org.eclipse.rdf4j.model.vocabulary.RDF;

final class LearnRdf4jHelper {

    static final Namespace ex = Values.namespace("ex", "http://example.enola.dev/");
    static final IRI artist = Values.iri(ex, "Artist");

    static Model picasso1() {
        IRI picasso = Values.iri(ex, "Picasso");
        Model model = new TreeModel();
        model.add(picasso, RDF.TYPE, artist);
        model.add(picasso, FOAF.FIRST_NAME, Values.literal("Pablo"));
        model.add(picasso, LOCN.LOCATION, Values.literal("Spain", "en"));
        BNode address = Values.bnode("b0");
        model.add(picasso, Values.iri(ex, "homeAddress"), address);
        model.add(address, Values.iri(ex, "street"), Values.literal("31 Art Gallery"));
        model.add(address, Values.iri(ex, "city"), Values.literal("Barcelona"));
        return model;
    }

    static Model dali1() {
        Model model = new TreeModel();
        IRI dali = Values.iri(ex, "Dalí");
        model.add(dali, RDF.TYPE, artist);
        model.add(dali, FOAF.FIRST_NAME, Values.literal("Salvador"));
        model.add(dali, FOAF.FIRST_NAME, Values.literal("Domingo"));
        model.add(dali, FOAF.FIRST_NAME, Values.literal("Felipe"));
        model.add(dali, FOAF.FIRST_NAME, Values.literal("Jacinto"));
        model.add(
                dali,
                Values.iri("https://schema.org/birthDate"),
                Values.literal("1904-05-11", Values.iri("https://schema.org/Date")));
        return model;
    }

    static Model picassoAndDali1() {
        var model = picasso1();
        model.addAll(dali1());
        return model;
    }

    static Model picassoAndDali2() {
        BNode address = Values.bnode("b0");
        return new ModelBuilder()
                .setNamespace("ex", "http://example.enola.dev/")
                .setNamespace(FOAF.NS)
                .subject("ex:Picasso")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.FIRST_NAME, "Pablo")
                .add(LOCN.LOCATION, Values.literal("Spain", "en"))
                .add("ex:homeAddress", address)
                .subject(address)
                .add("ex:street", "31 Art Gallery")
                .add("ex:city", "Barcelona")
                .subject("ex:Dalí")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.FIRST_NAME, "Salvador")
                .add(FOAF.FIRST_NAME, "Domingo")
                .add(FOAF.FIRST_NAME, "Felipe")
                .add(FOAF.FIRST_NAME, "Jacinto")
                .add(
                        "https://schema.org/birthDate",
                        Values.literal("1904-05-11", Values.iri("https://schema.org/Date")))
                .build();
    }

    private LearnRdf4jHelper() {}
}
