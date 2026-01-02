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
package dev.enola.thing.validation;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.Link;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.repo.ThingMemoryRepositoryRW;
import dev.enola.thing.repo.ThingRepositoryStore;

import org.junit.Test;

import java.util.List;

public class LinksValidatorTest {

    // TODO Add missing test coverage for (working) blank nodes, Iterables, URI instead Link

    Thing one = ImmutableThing.builder().iri("http://example.com/one").build();
    Thing two =
            ImmutableThing.builder()
                    .iri("http://example.com/two")
                    .set("http://example.com/property1", new Link(one.iri()))
                    .build();
    Thing bad1 =
            ImmutableThing.builder()
                    .iri("http://example.com/three")
                    .set("http://example.com/property1", new Link("http://example.com/MISSING"))
                    .build();
    Thing bad2 =
            ImmutableThing.builder()
                    .iri("http://example.com/four")
                    .set("http://example.com/property1", "hi", "http://example.com/MISSING")
                    .build();

    ThingRepositoryStore repo = new ThingMemoryRepositoryRW();
    TestCollector collector = new TestCollector();
    Validators v = new Validators(new LinksValidator(repo));

    @Test
    public void empty() {
        v.validate(repo, collector);
        repo.store(one);
        assertThat(collector.getDiagnostics()).isEmpty();
    }

    @Test
    public void aok() {
        repo.storeAll(List.of(one, two));
        v.validate(repo, collector);
        assertThat(collector.getDiagnostics()).isEmpty();
    }

    @Test
    public void bad() {
        repo.storeAll(List.of(one, two, bad1, bad2));
        v.validate(repo, collector);
        assertThat(collector.getDiagnostics()).hasSize(2);
    }
}
