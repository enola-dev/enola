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
package dev.enola.common.name;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NamedObjectProviders {

    // TODO public static NamedObjectProvider union(NamedObjectProvider... providers)

    public static NamedObjectStore newConcurrent() {
        return new NamedObjectStoreImpl(new ConcurrentHashMap<>());
    }

    public static NamedObjectStore newSingleThreaded() {
        return new NamedObjectStoreImpl(new HashMap<>());
    }

    public static NamedObjectProvider newImmutable(Map<String, ?> flatMap) {
        // Group the flat map's entries by the value's class
        Map<Class<?>, Map<String, Object>> groupedByClass =
                flatMap.entrySet().stream()
                        .collect(
                                Collectors.groupingBy(
                                        entry -> entry.getValue().getClass(),
                                        Collectors.toUnmodifiableMap(
                                                Map.Entry::getKey, Map.Entry::getValue)));
        return new NamedObjectStoreImpl(Collections.unmodifiableMap(groupedByClass));
        // TODO Use Guava ImmutableMap instead of JDK Collections.unmodifiableMap.
    }
}
