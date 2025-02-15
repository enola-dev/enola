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

import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.repo.ThingsBuilders;

import org.junit.Test;

import java.time.Instant;

public class JavaThingTest {

    @Test
    public void javaAPI() {
        javaAPI(new ProxyTBF(ImmutableThing.FACTORY));
    }

    private void javaAPI(TBF tbf) {
        HasSomething.Builder<HasSomething> builder = HasSomething.builder(tbf);

        builder.test("abc").a(123L).b(Instant.now()).iri("https://example.org/thing");
        HasSomething thing = builder.build();

        assertThat(thing.iri()).isEqualTo("https://example.org/thing");

        assertThat(thing.getString(TestVoc.SOMETHING.TEST)).isEqualTo("abc");
        assertThat(thing.test()).isEqualTo("abc");

        assertThat(thing.get(TestVoc.A.A, Long.class)).isEqualTo(123L);
        assertThat(thing.a()).isEqualTo(123L);

        assertThat(thing.get(TestVoc.B.B, Instant.class)).isInstanceOf(Instant.class);
        assertThat(thing.b()).isInstanceOf(Instant.class);
    }

    @Test
    public void typedThingsBuilder() {
        typedThingsBuilder(new ProxyTBF(ImmutableThing.FACTORY));
    }

    public void typedThingsBuilder(TBF tbf) {
        var typedThingsBuilder = new ThingsBuilders(tbf);

        var builder =
                typedThingsBuilder.getBuilder(
                        "https://example.org/thing",
                        HasSomething.Builder.class,
                        HasSomething.class);
        builder.test("hello, world");

        Thing thing = builder.build();
        assertThat(thing.getString(TestVoc.SOMETHING.TEST)).isEqualTo("hello, world");

        Thing thing2 = builder.build();
        assertThat(thing2.getString(TestVoc.SOMETHING.TEST)).isEqualTo("hello, world");

        // TODO HasSomething hasSomething = builder.build();
        // TODO assertThat(hasSomething.test()).isEqualTo("hello, world");
    }
}
