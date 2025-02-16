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
package dev.enola.thing.impl;

import com.google.common.collect.ImmutableCollection;

import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.Thing;

// Package-local (non-public) helpers
final class ImmutableObjects {

    static void check(Object object) {
        if ((object instanceof Iterable<?>) && !(object instanceof ImmutableCollection<?>))
            throw new IllegalStateException("Non-ImmutableCollection: " + object);
        if (object instanceof Thing)
            throw new IllegalStateException("Things cannot contain Things: " + object);
        if (object instanceof Thing.Builder)
            throw new IllegalStateException("Things cannot contain Thing.Builder: " + object);
        if (object instanceof PredicatesObjects.Builder)
            throw new IllegalStateException(
                    "Things cannot contain PredicatesObjects.Builder: " + object);
        if (object instanceof PredicatesObjects && !(object instanceof IImmutablePredicatesObjects))
            throw new IllegalStateException(
                    "Things cannot contain Non-IImmutablePredicatesObjects: " + object);
    }

    private ImmutableObjects() {}
}
