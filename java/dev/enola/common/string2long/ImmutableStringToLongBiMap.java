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

import static com.google.common.collect.ImmutableList.copyOf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable implementation of {@link StringToLongBiMap}.
 *
 * <p>The Builder of this class is NOT thread-safe, but the resulting built instance is (and it's
 * FASTER; e.g. faster than the {@link ConcurrentStringToLongBiMap}).
 *
 * <p>As currently implemented, this only actually supports up to {@link Integer#MAX_VALUE} (NOT
 * Long!) number of symbols. The API, however, does allow for future expansion of this
 * implementation to and other implementations which support more symbols.
 */
@Immutable
public class ImmutableStringToLongBiMap implements StringToLongBiMap {

    private final ImmutableMap<String, Integer> symbolsMap;
    private final ImmutableList<String> symbols;

    private ImmutableStringToLongBiMap(
            ImmutableMap<String, Integer> symbolsMap, ImmutableList<String> symbols) {
        this.symbolsMap = symbolsMap;
        this.symbols = symbols;
    }

    @Override // ~copy/paste from ConcurrentStringToLongBiMap
    public void get(String symbol, LongOrStringConsumer consumer) {
        var id = symbolsMap.get(symbol);
        if (id != null) consumer.longID(id);
        else consumer.string(symbol);
    }

    @Override
    public String get(long id) throws IllegalArgumentException {
        if (id >= 0 && id < symbols.size()) return symbols.get((int) id);
        else throw new IllegalArgumentException(Long.toUnsignedString(id));
    }

    @Override
    public long size() {
        return symbols.size();
    }

    @Override
    public Iterable<String> symbols() {
        return symbols;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static class BuilderImpl implements Builder {

        // TODO Is there EVER going to be any need to support long instead of int size? Nota bene:
        // ConcurrentStringToLongBiMap already supports up to [#MAX_VALUE] (not just Int) number of
        // symbols.

        private final Map<String, Integer> map = new HashMap<>();
        private int nextId = 0;

        @Override
        public long put(String symbol) {
            if (nextId == Integer.MAX_VALUE) throw new IllegalStateException();
            var id = map.get(symbol);
            if (id != null) return id;
            else {
                map.put(symbol, nextId);
                return nextId++;
            }
        }

        @Override
        public StringToLongBiMap build() {
            var size = map.size();
            var immutableMapBuilder = ImmutableMap.<String, Integer>builderWithExpectedSize(size);
            var array = new String[size];
            map.forEach(
                    (symbol, id) -> {
                        immutableMapBuilder.put(symbol, id);
                        if (array[id] != null) throw new IllegalStateException();
                        array[id] = symbol;
                    });
            return new ImmutableStringToLongBiMap(immutableMapBuilder.build(), copyOf(array));
        }
    }
}
