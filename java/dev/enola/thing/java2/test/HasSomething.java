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

import dev.enola.thing.Thing;
import dev.enola.thing.java2.TBF;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface HasSomething extends HasA, HasB {

    // TODO Take "https://example.org/test" from a KIRI-like gen. class

    default @Nullable String test() {
        return getString("https://example.org/test");
    }

    interface Builder<B extends HasSomething> // skipcq: JAVA-E0169
            extends HasA.Builder<B>, HasB.Builder<B>, Thing.Builder<B> {

        default Builder<B> test(String test) {
            set("https://example.org/test", test);
            return this;
        }

        @Override
        default Builder<B> a(Long test) {
            HasA.Builder.super.a(test);
            return this;
        }

        @Override
        default Builder<B> b(Instant test) {
            HasB.Builder.super.b(test);
            return this;
        }
    }

    static Builder<HasSomething> builder(TBF tbf) {
        return tbf.create(HasSomething.Builder.class, HasSomething.class);
    }
}
