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

/// Bidirectional map for common "Symbol" String to Long.
///
/// Useful e.g. for client-server I/O, or persistent storage; not needed in-memory.
///
/// Enola can use this e.g. for Thing IRI, including Datatype IRI, but also e.g. Language Codes.
public interface StringToLongBiMap {

    long get(String symbol) throws IllegalArgumentException;

    String get(long id) throws IllegalArgumentException;

    // ? Object getOrSame(String) returns Long OR same String
    // ? Object getOrSame(long)
    // ? Optional<String> getOptional(long)
    // ? Optional<Long> getOptional(String)

    long size();

    // ? void forEach(Consumer<String>), NOT BiConsumer<Long, String>), or just Iterable<String> ?
    // Write *IO, first. Or don't even have such methods, until needed…

    // skipcq: JAVA-E0169
    interface Builder extends dev.enola.common.Builder<StringToLongBiMap> {
        long put(String symbol);
    }
}
