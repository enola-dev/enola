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
package dev.enola.data;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

/**
 * Stores "store" (AKA "save" or you can "put") Ts in(to) them.
 *
 * <p>Stores which are also {@link Provider} typically internally actually are, or delegate to, a
 * {@link StoreKV}, but their external API is {@link #store(T value)} instead of {@link
 * StoreKV#store(K key, V value)} simply because they internally extract a key from T.
 */
public interface Store<B, T> {

    @CanIgnoreReturnValue
    B store(T item);

    @CanIgnoreReturnValue
    B store(Iterable<T> items);
}
