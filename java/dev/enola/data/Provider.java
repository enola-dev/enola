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

import com.google.errorprone.annotations.ThreadSafe;

/** Providers "provide" (AKA "load" or allow you to "get") a value (V), given a key (K). */
@ThreadSafe
public interface Provider<K, V> {

    /**
     * Get.
     *
     * @param key Key, never null
     * @return value, or null if there is no Value for the given key
     */
    // TODO @Nullable (from JSpecify) ? Or not... AlwaysThingProvider!
    V get(K key);
}
