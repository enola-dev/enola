/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentStringToLongBiMap implements StringToLongBiMap, StringToLongBiMap.Builder {

    private final AtomicLong nextId = new AtomicLong(0);
    private final ConcurrentHashMap<String, Long> stringToLongMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> longToStringMap = new ConcurrentHashMap<>();

    public static ConcurrentStringToLongBiMap builder() {
        return new ConcurrentStringToLongBiMap();
    }

    @Override
    public StringToLongBiMap build() {
        return this;
    }

    @Override
    public long put(String symbol) {
        long id = nextId.getAndIncrement();
        if (id == Long.MAX_VALUE) throw new IllegalStateException();

        if (stringToLongMap.putIfAbsent(symbol, id) != null) {
            throw new IllegalArgumentException("Symbol already exists: " + symbol);
        }

        longToStringMap.put(id, symbol);
        return id;
    }

    @Override
    public long get(String symbol) throws IllegalArgumentException {
        Long id = stringToLongMap.get(symbol);
        if (id == null) {
            throw new IllegalArgumentException("Symbol not found: " + symbol);
        }
        return id;
    }

    @Override
    public String get(long id) throws IllegalArgumentException {
        String symbol = longToStringMap.get(id);
        if (symbol == null) {
            throw new IllegalArgumentException("ID not found: " + id);
        }
        return symbol;
    }

    @Override
    public long size() {
        return nextId.get();
    }
}
