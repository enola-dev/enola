/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.model.w3.rdf.Property;
import dev.enola.model.w3.rdfs.Class;
import dev.enola.thing.KIRI;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.repo.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RDFSTriggersTest {

    @Rule
    public final TestRule tlcRule = TestTLCRule.of(TBF.class, new ProxyTBF(ImmutableThing.FACTORY));

    @Test
    public void thingMemoryRepositoryRW() {
        var trigger = new RDFSPropertyTrigger();
        Supplier<ThingRepositoryStore> repoSupplier =
                () -> {
                    var repo =
                            new AlwaysThingRepositoryStore(
                                    new ThingMemoryRepositoryRW(ImmutableList.of(trigger)));
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
                    var repo =
                            new AlwaysThingRepositoryStore(
                                    new ThingMemoryRepositoryROBuilder(ImmutableList.of(trigger)));
                    trigger.setRepo(repo);
                    return repo;
                };
        thingRepositoryStore(repoSupplier);
    }

    void thingRepositoryStore(Supplier<ThingRepositoryStore> repoSupplier) {
        check(repoSupplier, this::justOneProperty);
        check(repoSupplier, this::classAndProperties);
        check(repoSupplier, this::propertyClassProperty);
        check(repoSupplier, this::addRemove);
        check(repoSupplier, this::inverseOf);
        check(repoSupplier, this::propertyDomainProperty);
    }

    void check(
            Supplier<ThingRepositoryStore> repoSupplier,
            Consumer<ThingRepositoryStore> repoConsumer) {
        var repo = repoSupplier.get();
        try (var ctx = TLC.open().push(ThingProvider.class, repo)) {
            repoConsumer.accept(repo);
        }
    }

    void justOneProperty(ThingRepositoryStore repo) {
        var property =
                Property.builder()
                        .domain("http://example.org/AClass")
                        .iri("http://example.org/AProperty")
                        .build();
        repo.store(property);
        assertThat(repo.listIRI())
                .containsExactly("http://example.org/AProperty", "http://example.org/AClass");
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

    void inverseOf(ThingRepositoryStore repo) {
        // Intentionally use "raw" thing instead of Property interface!
        var inverseProperty =
                Property.builder()
                        .iri("http://example.org/inverseOf")
                        .domain(KIRI.RDF.PROPERTY)
                        .range(KIRI.RDF.PROPERTY)
                        .build();
        repo.store(inverseProperty);
        var propertyClass = repo.get(KIRI.RDF.PROPERTY, Class.class);
        assertThat(propertyClass.hasRdfsClassProperty("http://example.org/inverseOf")).isTrue();
    }

    /**
     * Non-regression for the java.lang.IllegalStateException: Recursive update at
     * java.base/java.util.concurrent.ConcurrentHashMap.putVal(ConcurrentHashMap.java:1063) in case
     * of a recursive trigger.
     */
    void propertyDomainProperty(ThingRepositoryStore repo) {
        var iri = "http://example.org/weirdo";
        var weirdo1 = Property.builder().iri(iri).domain(iri).build();
        repo.store(weirdo1);

        var weirdo2 = Property.builder().iri(KIRI.RDF.PROPERTY).domain(KIRI.RDF.PROPERTY).build();
        repo.store(weirdo2);
    }
}
