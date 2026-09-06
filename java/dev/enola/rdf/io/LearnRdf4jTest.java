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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.jupiter.api.Test;

class LearnRdf4jTest {

    // TODO Ontology to define what's "valid" for http://www.w3.org/ns/locn#location?

    // https://rdf4j.org/documentation/tutorials/getting-started/

    // Namespace
    // String ex = "http://example.enola.dev/";
    Namespace ex = Values.namespace("ex", "http://example.enola.dev/");
    IRI artist = Values.iri(ex, "Artist");

    Model picasso1() {
        return LearnRdf4jHelper.picasso1();
    }

    Model dali1() {
        return LearnRdf4jHelper.dali1();
    }

    Model picassoAndDali1() {
        return LearnRdf4jHelper.picassoAndDali1();
    }

    Model picassoAndDali2() {
        return LearnRdf4jHelper.picassoAndDali2();
    }

    @Test
    void testRDF() {
        var picassoAndDali1 = picassoAndDali1();
        var picassoAndDali2 = picassoAndDali2();
        assertThat(picassoAndDali1).isEqualTo(picassoAndDali2);
    }

    @Test
    void testRepository() {
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
