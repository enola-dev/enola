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
package dev.enola.core.type;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.protobuf.TypeRegistry;

import dev.enola.core.meta.proto.Type;
import dev.enola.data.RepositoryBuilder;
import dev.enola.data.Store;

/**
 * Builds Repository of {@link Type}.
 *
 * <p>Not to be confused with and totally unrelated to {@link TypeRegistry}.
 */
public class TypeRepositoryBuilder extends RepositoryBuilder<Type>
        implements Store<TypeRepositoryBuilder, Type.Builder> {

    @Override
    @CanIgnoreReturnValue
    public TypeRepositoryBuilder store(Type.Builder type) {
        require(type.getUri(), "uri");
        // TODO setUrl(...), based on some sort of baseURL to the Web UI
        var name = require(type.getName(), "name");
        store(name, type.build());
        return this;
    }

    public TypeRepositoryBuilder addAllTypes(Iterable<Type.Builder> types) {
        for (Type.Builder type : types) {
            store(type);
        }
        return this;
    }
}
