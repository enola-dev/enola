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
package dev.enola.thing.java2.test;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java2.ProxyTBF;
import dev.enola.thing.repo.TypedThingsBuilder;

import org.junit.Test;

import java.time.Instant;

public class JavaThingTest {

    @Test
    public void javaAPI() {
        var proxyTBF = new ProxyTBF(ImmutableThing.FACTORY);
        HasSomething.Builder<HasSomething> builder =
                proxyTBF.create(HasSomething.Builder.class, HasSomething.class);

        builder.test("abc").a(123L).b(Instant.now()).iri("https://example.org/thing");
        HasSomething thing = builder.build();

        assertThat(thing.iri()).isEqualTo("https://example.org/thing");

        assertThat(thing.getString("https://example.org/test")).isEqualTo("abc");
        assertThat(thing.test()).isEqualTo("abc");

        assertThat(thing.get("https://example.org/a", Long.class)).isEqualTo(123L);
        assertThat(thing.a()).isEqualTo(123L);

        assertThat(thing.get("https://example.org/b", Instant.class)).isInstanceOf(Instant.class);
        assertThat(thing.b()).isInstanceOf(Instant.class);
    }

    @Test
    public void typedThingsBuilder() {
        var typedThingsBuilder =
                new TypedThingsBuilder<HasSomething, HasSomething.Builder>(
                        new ProxyTBF(ImmutableThing.FACTORY));

        var builder =
                typedThingsBuilder.getBuilder(
                        "https://example.org/thing",
                        HasSomething.Builder.class,
                        HasSomething.class);
        builder.test("hello, world");

        Thing thing = builder.build();
        assertThat(thing.getString("https://example.org/test")).isEqualTo("hello, world");

        Thing thing2 = builder.build();
        assertThat(thing2.getString("https://example.org/test")).isEqualTo("hello, world");

        // TODO HasSomething hasSomething = builder.build();
        // TODO assertThat(hasSomething.test()).isEqualTo("hello, world");
    }
}
