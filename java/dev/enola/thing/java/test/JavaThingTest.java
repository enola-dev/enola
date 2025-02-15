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
package dev.enola.thing.java.test;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.repo.ThingsBuilders;

import org.junit.Test;

import java.time.Instant;

public class JavaThingTest {

    @Test
    public void proxyTBF() {
        checkTBF(new ProxyTBF(ImmutableThing.FACTORY));
    }

    @Test
    public void hasSomethingTBF() {
        checkTBF(new HasSomethingTBF());
    }

    private void checkTBF(TBF tbf) {
        checkHasSomethingBuilder(tbf);
        checkThingsBuilders(tbf);
    }

    private void checkHasSomethingBuilder(TBF tbf) {
        var builder = HasSomething.builder(tbf);
        builder.iri("https://example.org/thing");
        checkHasSomethingBuilder(builder);
    }

    private void checkThingsBuilders(TBF tbf) {
        var thingsBuilders = new ThingsBuilders(tbf);
        var builder =
                thingsBuilders.getBuilder(
                        "https://example.org/thing",
                        HasSomething.Builder.class,
                        HasSomething.class);
        checkHasSomethingBuilder(builder);
    }

    private void checkHasSomethingBuilder(HasSomething.Builder<HasSomething> builder) {
        builder.test("abc").a(123L).b(Instant.now()).iri("https://example.org/thing");

        HasSomething thing = builder.build();
        checkHasSomething(thing);

        HasSomething thing2 = builder.build();
        checkHasSomething(thing2);
    }

    private void checkHasSomething(HasSomething thing) {
        assertThat(thing.iri()).isEqualTo("https://example.org/thing");

        assertThat(thing.getString(TestVoc.SOMETHING.TEST)).isEqualTo("abc");
        assertThat(thing.test()).isEqualTo("abc");

        assertThat(thing.get(TestVoc.A.A, Long.class)).isEqualTo(123L);
        assertThat(thing.a()).isEqualTo(123L);

        assertThat(thing.get(TestVoc.B.B, Instant.class)).isInstanceOf(Instant.class);
        assertThat(thing.b()).isInstanceOf(Instant.class);
    }
}
