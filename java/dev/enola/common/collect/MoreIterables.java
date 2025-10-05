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
package dev.enola.common.collect;

import com.google.common.collect.ImmutableList;

import dev.enola.common.function.CheckedConsumer;
import dev.enola.common.function.Sneaker;

import java.util.Collection;
import java.util.OptionalInt;

public final class MoreIterables {

    public static <T, E extends Exception> void forEach(
            Iterable<T> iterable, CheckedConsumer<T, E> action) throws E {
        iterable.forEach(Sneaker.sneakyConsumer(action));
    }

    /**
     * Size of an {@link Iterable}, if there is an efficient way of obtaining it. Useful for
     * optimizations. See also {@link com.google.common.collect.Iterables#size(Iterable)}.
     */
    public static OptionalInt sizeIfKnown(Iterable<?> iterable) {
        return (iterable instanceof Collection)
                ? OptionalInt.of(((Collection<?>) iterable).size())
                : OptionalInt.empty();
    }

    /**
     * Convert an {@link Iterable} to a {@link Collection}.
     *
     * @param iterable an {@link Iterable} (of any kind)
     * @return Collection which may just be the argument if that {@link Iterable} was a Collection
     *     already (as an optimization), or otherwise an {@link ImmutableList}. Note that this means
     *     that callers should always assume that the returned collection is logically immutable.
     *     Due to the optimization for Iterables that are Collections, we just cannot (efficiently)
     *     actually declare the return type to be an ImmutableCollection.
     * @param <T> Type
     */
    public static <T> Collection<T> toCollection(Iterable<T> iterable) {
        if (iterable instanceof Collection<T> collection) return collection;
        else return ImmutableList.copyOf(iterable);
    }

    private MoreIterables() {}
}
