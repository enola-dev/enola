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
package dev.enola.common.name;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ImmutableNamedTypedObjectProvider<T> implements NamedTypedObjectProvider<T> {

    private final ImmutableMap<String, T> map;

    public ImmutableNamedTypedObjectProvider(Map<String, T> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    @Override
    public Set<String> names() {
        return map.keySet();
    }

    @Override
    public Optional<T> opt(String name) {
        return Optional.ofNullable(map.get(name));
    }
}
