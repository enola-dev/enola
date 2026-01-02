/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.datatype;

import static java.util.Objects.requireNonNull;

import dev.enola.common.convert.ObjectToStringBiConverter;

import java.util.Optional;
import java.util.regex.Pattern;

public record ImmutableDatatype<T>(
        String iri,
        Optional<Pattern> pattern,
        ObjectToStringBiConverter<T> stringConverter,
        Optional<Class<T>> javaType)
        implements Datatype<T> {

    public ImmutableDatatype {
        requireNonNull(iri);
        requireNonNull(pattern);
        requireNonNull(stringConverter);
        requireNonNull(javaType);
    }

    public ImmutableDatatype(String iri) {
        this(iri, Optional.empty(), new MissingObjectToStringBiConverter<T>(iri), Optional.empty());
    }

    public ImmutableDatatype(
            String iri, ObjectToStringBiConverter<T> stringConverter, Class<T> javaType) {
        this(iri, Optional.empty(), stringConverter, Optional.of(javaType));
    }

    public ImmutableDatatype(
            String iri,
            ObjectToStringBiConverter<T> stringConverter,
            Class<T> javaType,
            Pattern pattern) {
        this(iri, Optional.of(pattern), stringConverter, Optional.of(javaType));
    }

    public ImmutableDatatype(
            String iri,
            ObjectToStringBiConverter<T> stringConverter,
            Class<T> javaType,
            String pattern) {
        this(iri, stringConverter, javaType, Pattern.compile(pattern));
    }

    @Override
    public DatatypeBuilder<T> child() {
        return new DatatypeBuilder<>(this);
    }
}
