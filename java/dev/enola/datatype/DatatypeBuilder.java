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

import dev.enola.common.Builder;
import dev.enola.common.convert.ObjectToStringBiConverter;

import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Pattern;

public class DatatypeBuilder<T> implements Builder<Datatype<T>> {

    private String iri;
    private final @Nullable Pattern pattern;
    private final ObjectToStringBiConverter<T> stringConverter;
    private final @Nullable Class<T> javaType;

    DatatypeBuilder(Datatype<T> clone) {
        this.iri = clone.iri();
        this.pattern = clone.pattern().orElse(null);
        this.stringConverter = clone.stringConverter();
        this.javaType = clone.javaType().orElse(null);
    }

    public DatatypeBuilder<T> iri(String iri) {
        this.iri = requireNonNull(iri);
        return this;
    }

    // TODO setters for (all?) other methods of Datatype

    @Override
    public Datatype<T> build() {
        return new ImmutableDatatype<T>(
                iri, Optional.ofNullable(pattern), stringConverter, Optional.ofNullable(javaType));
    }
}
