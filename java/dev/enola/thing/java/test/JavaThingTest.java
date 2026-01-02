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
package dev.enola.thing.java.test;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;

import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.impl.MutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.java.TBFChain;

import org.junit.Test;

import java.lang.reflect.Proxy;
import java.time.Instant;

public class JavaThingTest {

    @Test
    public void mutableProxyTBF() {
        checkTBF(new ProxyTBF(MutableThing.FACTORY));
    }

    @Test
    public void immutableProxyTBF() {
        checkTBF(new ProxyTBF(ImmutableThing.FACTORY));
    }

    @Test
    public void testSomethingTBF() {
        checkTBF(new TestSomethingTBF());
    }

    @Test
    public void testSomethingBuilder() {
        // TestSomething.builder() === new TestSomethingTBF(), as above
        var builder = TestSomething.builder();
        builder.iri("https://example.org/thing");
        checkTestSomethingBuilder(builder);
    }

    @Test
    public void chainTBF1() {
        checkTBF(new TBFChain(ImmutableList.of(new TestSomethingTBF())));
    }

    @Test
    public void chainTBF2() {
        var tbf =
                new TBFChain(
                        ImmutableList.of(
                                new TestSomethingTBF(),
                                new ProxyTBF(ImmutableThing.FACTORY),
                                ImmutableThing.FACTORY));
        checkTBF(tbf);

        var testSomethingBuilder = tbf.create(TestSomething.Builder.class, TestSomething.class);
        assertThat(testSomethingBuilder).isNotInstanceOf(Proxy.class);
        assertThat(testSomethingBuilder).isNotNull();

        var hasABuilder = tbf.create(HasA.Builder.class, HasA.class);
        assertThat(hasABuilder).isInstanceOf(Proxy.class);

        var immutableThingBuilder = tbf.create();
        assertThat(immutableThingBuilder).isInstanceOf(ImmutableThing.Builder.class);
        assertThat(immutableThingBuilder).isNotInstanceOf(Proxy.class);
    }

    private void checkTBF(TBF tbf) {
        checkTestSomethingBuilder(tbf);
        checkCreateFromType(tbf);
    }

    @SuppressWarnings({"unchecked"})
    private void checkCreateFromType(TBF tbf) {
        var builder = (TestSomething.Builder<?>) tbf.create(TestSomething.CLASS_IRI);
        builder.iri("https://example.org/thing");
        checkTestSomethingBuilder((TestSomething.Builder<TestSomething>) builder);
    }

    private void checkTestSomethingBuilder(TBF tbf) {
        var builder = TestSomething.builder(tbf);
        builder.iri("https://example.org/thing");
        checkTestSomethingBuilder(builder);
    }

    private void checkTestSomethingBuilder(TestSomething.Builder<TestSomething> builder) {
        builder.test("abc").a(123L).b(Instant.now()).iri("https://example.org/thing");

        TestSomething thing = builder.build();
        checkTestSomething(thing);

        TestSomething thing2 = builder.build();
        checkTestSomething(thing2);
    }

    private void checkTestSomething(TestSomething thing) {
        assertThat(thing.iri()).isEqualTo("https://example.org/thing");

        assertThat(thing.getString(TestSomething.TEST_PROPERTY_IRI)).isEqualTo("abc");
        assertThat(thing.test()).isEqualTo("abc");

        assertThat(thing.get(HasA.IRI, Long.class)).isEqualTo(123L);
        assertThat(thing.a()).isEqualTo(123L);

        assertThat(thing.get(HasB.IRI, Instant.class)).isInstanceOf(Instant.class);
        assertThat(thing.b()).isInstanceOf(Instant.class);

        assertThat(thing.typesIRIs().iterator().next().toString())
                .isEqualTo(TestSomething.CLASS_IRI);

        var builder = thing.copy();
        builder.test("hello, world");
        var rebuilt = builder.build();
        assertThat(rebuilt.test()).isEqualTo("hello, world");
        assertThat(rebuilt.a()).isEqualTo(123L);
        assertThat(rebuilt.b()).isInstanceOf(Instant.class);
        assertThat(rebuilt.typesIRIs().iterator().next().toString())
                .isEqualTo(TestSomething.CLASS_IRI);
    }
}
