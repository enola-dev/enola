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
package dev.enola.data.id;

import com.google.errorprone.annotations.Immutable;

import dev.enola.common.ByteSeq;
import dev.enola.common.convert.ObjectToStringBiConverters;
import dev.enola.data.iri.IRIConverter;

@Immutable
public class MultibaseIRI extends IDIRI<ByteSeq> {

    // Intentionally different from MultibaseResource's multibase: scheme.
    private static final String SCHEME = "mb";

    static final ConverterX<MultibaseIRI, ByteSeq> CONVERTER =
            new ConverterX<>(SCHEME, ObjectToStringBiConverters.MULTIBASE) {
                @Override
                protected MultibaseIRI create(ByteSeq id) {
                    return new MultibaseIRI(id);
                }
            };

    public static MultibaseIRI random() {
        return random(16);
    }

    public static MultibaseIRI random(int size) {
        return new MultibaseIRI(size);
    }

    public static MultibaseIRI parse(String multibase) {
        return CONVERTER.convertFrom(multibase);
    }

    public static MultibaseIRI of(ByteSeq id) {
        return new MultibaseIRI(id);
    }

    private MultibaseIRI(int size) {
        this(ByteSeq.random(size));
    }

    private MultibaseIRI(ByteSeq bs) {
        super(bs);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected IRIConverter<MultibaseIRI> iriConverter() {
        return CONVERTER;
    }

    @Override
    protected boolean isComparableTo(Object other) {
        return other instanceof MultibaseIRI;
    }
}
