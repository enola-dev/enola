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
package dev.enola.thing.namespace;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.data.iri.namespace.repo.ImmutableNamespace;
import dev.enola.rdf.io.RdfLoader;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;

public class ThingNamespaceRepositoryTest {

    // TODO Cover CachingNamespaceRepository, here and/or in other tests?

    @Rule public SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    @Rule public final TestRule tlcRule = EnolaTestTLCRules.BASIC;

    @Test
    public void empty() {
        ThingNamespaceRepository ns = new ThingNamespaceRepository();
        assertThat(ns.getIRI("enola")).isEmpty();
        assertThat(ns.listIRI()).isEmpty();
        assertThat(ns.stream()).isEmpty();
        assertThat(ns.get("https://enola.dev")).isNull();
    }

    @Test
    public void namespacesTTL() throws IOException {
        var store = new ThingMemoryRepositoryROBuilder();
        assertThat(new RdfLoader().load("classpath:/enola.dev/namespaces.ttl", store)).isTrue();
        ThingNamespaceRepository ns = new ThingNamespaceRepository(store.build());

        assertThat(ns.getIRI("enola")).hasValue("https://enola.dev/");
        assertThat(ns.listIRI()).contains("https://enola.dev/");
        assertThat(ns.get("https://enola.dev/"))
                .isEqualTo(new ImmutableNamespace("enola", "https://enola.dev/"));
    }
}
