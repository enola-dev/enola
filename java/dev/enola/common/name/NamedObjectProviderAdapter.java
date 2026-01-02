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

import java.util.List;
import java.util.Optional;

public class NamedObjectProviderAdapter<T> implements NamedObjectProvider {

    private final Class<T> clazz;
    private final NamedTypedObjectProvider<T> delegate;

    public NamedObjectProviderAdapter(Class<T> clazz, NamedTypedObjectProvider<T> delegate) {
        this.clazz = clazz;
        this.delegate = delegate;
    }

    @Override
    public Iterable<String> names(Class<?> clazz) {
        if (this.clazz.equals(clazz)) return delegate.names();
        return List.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> Optional<X> opt(String name, Class<X> clazz) {
        if (this.clazz.equals(clazz)) return (Optional<X>) delegate.opt(name);
        return Optional.empty();
    }
}
