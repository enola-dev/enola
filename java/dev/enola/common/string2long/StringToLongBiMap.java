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

/// Bidirectional map for common "Symbol" String to Long.
///
/// Useful e.g. for "compressing" client-server I/O, or persistent storage; not needed in-memory.
///
/// Enola can use this e.g. for Thing IRI, including Datatype IRI, but also e.g. Language Codes.
///
/// See also string2long.ts
public interface StringToLongBiMap {

    interface LongOrStringConsumer {
        void longID(long id);

        void string(String symbol);
    }

    // For what we intend to use this for (e.g. JSON gen, or oneof Proto), this signature is much
    // better than Object getOrSame(String) & Object getOrSame(long) [which would lead to
    // instanceof], or Optional<Long> getOptional(String) & Optional<String> getOptional(long)
    // [which would constantly "double look-up" instead of this much more efficient single op].
    void get(String symbol, LongOrStringConsumer consumer);

    String get(long id) throws IllegalArgumentException;

    long size();

    /** Symbols, ordered by their Long ID. */
    Iterable<String> symbols();

    // skipcq: JAVA-E0169
    interface Builder extends dev.enola.common.Builder<StringToLongBiMap> {
        long put(String symbol);
    }
}
