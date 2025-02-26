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

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.IImmutableThing;
import dev.enola.thing.java.TBF;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

// TODO Generate this, from a model
public interface TestSomething extends HasA, HasB, IImmutableThing {
    // TODO TestSomething extends HasClass? But then it needs to be moved... is it so central?

    String TEST_PROPERTY_IRI = "https://example.org/test";
    String CLASS_IRI = "https://example.org/TestSomething";

    default @Nullable String test() {
        return getString(TestSomething.TEST_PROPERTY_IRI);
    }

    @Override
    Builder<? extends TestSomething> copy();

    // TODO TestSomething.Builder extends HasClass.Builder?!
    interface Builder<B extends TestSomething> // skipcq: JAVA-E0169
            extends HasA.Builder<B>, HasB.Builder<B>, Thing.Builder<B> {

        default Builder<B> test(String test) {
            set(TestSomething.TEST_PROPERTY_IRI, test);
            return this;
        }

        @Override
        @CanIgnoreReturnValue
        default Builder<B> a(Long test) {
            HasA.Builder.super.a(test);
            return this;
        }

        @Override
        @CanIgnoreReturnValue
        default Builder<B> b(Instant test) {
            HasB.Builder.super.b(test);
            return this;
        }

        @Override
        @CanIgnoreReturnValue
        Builder<B> iri(String iri);
    }

    @SuppressWarnings("unchecked")
    static Builder<TestSomething> builder(TBF tbf) {
        return tbf.create(TestSomething.Builder.class, TestSomething.class);
        // TODO Set type() to CLASS_IRI
    }

    @SuppressWarnings("unchecked")
    static Builder<TestSomething> builder() {
        return builder(new HasSomethingTBF());
    }
}
