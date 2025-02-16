/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.infer.rdf;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;

import dev.enola.common.context.TLC;
import dev.enola.model.w3.rdf.Property;
import dev.enola.model.w3.rdfs.Class;
import dev.enola.thing.repo.*;

import org.junit.Test;

import java.util.function.Supplier;

public class RDFSTriggersTest {

    @Test
    public void thingMemoryRepositoryRW() {
        var trigger = new RDFSPropertyTrigger();
        Supplier<ThingRepositoryStore> repoSupplier =
                () -> {
                    var repo = new ThingMemoryRepositoryRW(ImmutableList.of(trigger));
                    trigger.setRepo(repo);
                    return repo;
                };
        thingRepositoryStore(repoSupplier);
    }

    @Test
    public void thingMemoryRepositoryROBuilder() {
        var trigger = new RDFSPropertyTrigger();
        Supplier<ThingRepositoryStore> repoSupplier =
                () -> {
                    var repo = new ThingMemoryRepositoryROBuilder(ImmutableList.of(trigger));
                    trigger.setRepo(repo);
                    return repo;
                };
        thingRepositoryStore(repoSupplier);
    }

    void thingRepositoryStore(Supplier<ThingRepositoryStore> repoSupplier) {
        var repo = repoSupplier.get();
        try (var ctx = TLC.open().push(ThingProvider.class, repo)) {
            justOneProperty(repo);
        }

        repo = repoSupplier.get();
        try (var ctx = TLC.open().push(ThingProvider.class, repo)) {
            classAndProperties(repo);
        }

        repo = repoSupplier.get();
        try (var ctx = TLC.open().push(ThingProvider.class, repo)) {
            propertyClassProperty(repo);
        }

        repo = repoSupplier.get();
        try (var ctx = TLC.open().push(ThingProvider.class, repo)) {
            addRemove(repo);
        }
    }

    void justOneProperty(ThingRepositoryStore repo) {
        var property =
                Property.builder()
                        .domain("http://example.org/AClass")
                        .iri("http://example.org/AProperty")
                        .build();
        repo.store(property);
        // TODO Or should we expect it to contain both the Property and the Class, now...
        assertThat(repo.listIRI()).containsExactly("http://example.org/AProperty");
    }

    void classAndProperties(ThingRepositoryStore repo) {
        repo.store(Class.builder().iri("http://example.org/AClass").build());
        assertThat(repo.get("http://example.org/AClass", Class.class)).isInstanceOf(Class.class);

        repo.store(
                Property.builder()
                        .domain("http://example.org/AClass")
                        .iri("http://example.org/AProperty")
                        .build());
        assertThat(repo.listIRI())
                .containsExactly("http://example.org/AProperty", "http://example.org/AClass");
        assertThat(
                        repo.get("http://example.org/AClass", Class.class)
                                .hasRdfsClassProperty("http://example.org/AProperty"))
                .isTrue();
    }

    void propertyClassProperty(ThingRepositoryStore repo) {
        // TODO Create 2 properties (this won't work until Class.Builder.addRdfsClassProperty fixed)
    }

    void addRemove(ThingRepositoryStore repo) {
        // TODO Test removing a property
    }

    // TODO void changeDomain(ThingRepositoryStore repo) {
}
