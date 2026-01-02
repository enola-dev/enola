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

import static com.google.common.truth.Truth.assertThat;

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
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Test;

public class LearnRdf4jTest {

    // TODO Ontology to define what's "valid" for http://www.w3.org/ns/locn#location?

    // https://rdf4j.org/documentation/tutorials/getting-started/

    // Namespace
    // String ex = "http://example.enola.dev/";
    Namespace ex = Values.namespace("ex", "http://example.enola.dev/");
    IRI artist = Values.iri(ex, "Artist");

    Model picasso1() {
        // Create IRIs for the resources we want to add.
        IRI picasso = Values.iri(ex, "Picasso");

        // Create a new, empty Model object.
        Model model = new TreeModel();

        // Add our first statement: Picasso is an Artist
        model.add(picasso, RDF.TYPE, artist);

        // Second statement: Picasso's first name is "Pablo".
        model.add(picasso, FOAF.FIRST_NAME, Values.literal("Pablo"));

        // Terzo
        model.add(picasso, LOCN.LOCATION, Values.literal("Spain", "en"));
        // TODO model.add(picasso1, LOCN.LOCATION, Values.literal("España", "es"));

        BNode address = Values.bnode("b0");
        model.add(picasso, Values.iri(ex, "homeAddress"), address);
        model.add(address, Values.iri(ex, "street"), Values.literal("31 Art Gallery"));
        model.add(address, Values.iri(ex, "city"), Values.literal("Barcelona"));

        return model;
    }

    Model dali1() {
        Model model = new TreeModel();
        IRI dali = Values.iri(ex, "Dalí");
        model.add(dali, RDF.TYPE, artist);
        // The spanish really do make sure that their names are UUIDs... ;-)
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

    Model picassoAndDali1() {
        var model = picasso1();
        model.addAll(dali1());
        return model;
    }

    Model picassoAndDali2() {
        BNode address = Values.bnode("b0");
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
                // The spanish really do make sure that their names are UUIDs... ;-)
                .add(FOAF.FIRST_NAME, "Salvador")
                .add(FOAF.FIRST_NAME, "Domingo")
                .add(FOAF.FIRST_NAME, "Felipe")
                .add(FOAF.FIRST_NAME, "Jacinto")
                .add(
                        "https://schema.org/birthDate",
                        Values.literal("1904-05-11", Values.iri("https://schema.org/Date")))
                .build();
    }

    @Test
    public void testRDF() {
        var picassoAndDali1 = picassoAndDali1();
        var picassoAndDali2 = picassoAndDali2();
        assertThat(picassoAndDali1).isEqualTo(picassoAndDali2);
    }

    @Test
    public void testRepository() {
        Repository repo = new SailRepository(new MemoryStore());
        var vf = repo.getValueFactory();

        /* try (var c = repo.getConnection()) {
            c.begin(IsolationLevels.READ_COMMITTED);
            var picasso = picassoAndDali1();
            c.add(picasso);
            c.commit();
        } catch (RepositoryException e) { conn.rollback(); } */
        // TODO Write a helper which allows setting IsolationLevel!
        Repositories.consume(
                repo,
                c -> {
                    var picasso = picassoAndDali1();
                    c.add(picasso);
                });

        var subject = vf.createIRI("http://example.enola.dev/Dalí");
        /* try (var c = repo.getConnection()) {
            // Nota bene: This wouldn't work for Picasso - because of the homeAddress BlankNode!
            try (var statements = c.getStatements(subject, null, null, true)) {
                // NO NEED: c.enableDuplicateFilter();
                Model dali = QueryResults.asModel(statements);
                assertThat(dali).isEqualTo(dali1());
            }
        } */
        Model dali =
                Repositories.get(
                        repo,
                        c -> {
                            try (var statements = c.getStatements(subject, null, null, true)) {
                                // NO NEED: c.enableDuplicateFilter();
                                return QueryResults.asModel(statements);
                            }
                        });
        assertThat(dali).isEqualTo(dali1());
    }
}
