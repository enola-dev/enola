/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import org.jspecify.annotations.Nullable;

public interface HasA extends Thing {

    default @Nullable Long a() {
        return get("https://example.org/a", Long.class);
    }

    interface Builder<B extends HasA> extends Thing.Builder<B> { // skipcq: JAVA-E0169

        default HasA.Builder<B> a(Long test) {
            set("https://example.org/a", test);
            return this;
        }
    }
}
