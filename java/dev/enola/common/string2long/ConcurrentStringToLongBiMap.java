/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.string2long;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/// Concurrency safe implementation of [StringToLongBiMap.Builder].
///
/// If you only need a thread-safe implementation of [StringToLongBiMap] but can do with
/// the Builder (!) not being so, then prefer using the [ImmutableStringToLongBiMap].
///
/// This implementation supports up to [java.lang.Long#MAX_VALUE] (not just Int) number of symbols.
///
/// @author <a href="https://www.vorburger.ch">Michael Vorburger.ch</a> with input from Google
///     Gemini Pro 1.5!
public class ConcurrentStringToLongBiMap implements StringToLongBiMap, StringToLongBiMap.Builder {

    // Nota bene: Guava does not provide a concurrent BiMap...
    private final AtomicLong nextId = new AtomicLong(0);
    private final Map<String, Long> stringToLongMap = new ConcurrentHashMap<>();
    private final Map<Long, String> longToStringMap = new ConcurrentSkipListMap<>();

    public static ConcurrentStringToLongBiMap builder() {
        return new ConcurrentStringToLongBiMap();
    }

    @Override
    public StringToLongBiMap build() {
        return this;
    }

    @Override
    public long put(String symbol) {
        long currentId = nextId.get();
        Long existingId = stringToLongMap.putIfAbsent(symbol, currentId);
        if (existingId != null) {
            return existingId;
        }

        // Spin lock!
        while (!nextId.compareAndSet(currentId, currentId + 1)) {
            currentId = nextId.get();
            existingId = stringToLongMap.putIfAbsent(symbol, currentId);
            if (existingId != null) {
                return existingId;
            }
        }

        // This implementation is thread-safe for the long ID and size.
        // There is, however, a race condition between concurrent [#put(String)] and [#get(long)]
        // operations.
        // This is acceptable because only an ID returned from `put` guarantees that the
        // mapping is visible for later `get()` operations.
        longToStringMap.put(currentId, symbol);
        return currentId;
    }

    @Override // ~copy/paste from ImmutableStringToLongBiMap
    public void get(String symbol, LongOrStringConsumer consumer) {
        Long id = stringToLongMap.get(symbol);
        if (id != null) consumer.longID(id);
        else consumer.string(symbol);
    }

    @Override
    public String get(long id) throws IllegalArgumentException {
        String symbol = longToStringMap.get(id);
        if (symbol == null) {
            throw new IllegalArgumentException("ID not found: " + Long.toUnsignedString(id));
        }
        return symbol;
    }

    @Override
    public long size() {
        return nextId.get();
    }

    @Override
    public Iterable<String> symbols() {
        return longToStringMap.values();
    }
}
