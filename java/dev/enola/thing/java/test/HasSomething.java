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
// TODO Rename HasSomething to Something, because it's a RDF Class, not Property!
public interface HasSomething extends HasA, HasB, IImmutableThing {
    // TODO HasSomething extends HasType? But then it needs to be moved... is it so central?

    default @Nullable String test() {
        return getString(TestVoc.SOMETHING.TEST);
    }

    @Override
    Builder<? extends HasSomething> copy();

    // TODO HasSomething.Builder extends HasType.Builder?!
    interface Builder<B extends HasSomething> // skipcq: JAVA-E0169
            extends HasA.Builder<B>, HasB.Builder<B>, Thing.Builder<B> {

        default Builder<B> test(String test) {
            set(TestVoc.SOMETHING.TEST, test);
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
    static Builder<HasSomething> builder(TBF tbf) {
        return tbf.create(HasSomething.Builder.class, HasSomething.class);
    }

    @SuppressWarnings("unchecked")
    static Builder<HasSomething> builder() {
        return builder(new HasSomethingTBF());
    }
}
