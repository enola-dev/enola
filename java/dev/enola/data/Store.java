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
package dev.enola.data;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

/**
 * Stores "store" (AKA "save" or you can "put") Ts in(to) them.
 *
 * <p>Stores which are also {@link Provider} typically internally actually are, or delegate to, a
 * {@link StoreKV}, but their external API is {@link #store(T value)} instead of {@link
 * StoreKV#store(Object, Object)} simply because they internally extract a key from T.
 */
public interface Store<T> {

    // TODO Combine #store() and #merge() into single store(), or save() [?], after all?!

    // TODO Fix inconsistency of some methods returning B but others void

    /**
     * Merge a T into this store.
     *
     * <p>If this store does not already have this T, then this does the same as {@link
     * #store(Object)}.
     *
     * <p>Otherwise, an implementation specific strategy "merges" the existing and new T in the
     * store.
     */
    void merge(T item);

    /**
     * Store a T.
     *
     * @throws IllegalArgumentException if this store already has this T
     */
    @CanIgnoreReturnValue
    Store<T> store(T item);

    /** Store multiple Ts; see {@link #store(Object)}. */
    @CanIgnoreReturnValue
    default Store<T> storeAll(Iterable<T> items) {
        for (T item : items) {
            store(item);
        }
        return this;
    }
}
