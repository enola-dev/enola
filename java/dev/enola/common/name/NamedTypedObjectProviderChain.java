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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.Set;

public class NamedTypedObjectProviderChain<T> implements NamedTypedObjectProvider<T> {

    private final ImmutableList<NamedTypedObjectProvider<T>> chain;
    private final Set<String> names;

    public NamedTypedObjectProviderChain(NamedTypedObjectProvider<T>... chain) {
        this.chain = ImmutableList.copyOf(chain);
        var namesBuilder = ImmutableSet.<String>builder();
        for (var provider : chain) {
            namesBuilder.addAll(provider.names());
        }
        names = namesBuilder.build();
    }

    @Override
    public Set<String> names() {
        return names;
    }

    @Override
    public Optional<T> opt(String name) {
        for (var provider : chain) {
            var opt = provider.opt(name);
            if (opt.isPresent()) return opt;
        }
        return Optional.empty();
    }
}
