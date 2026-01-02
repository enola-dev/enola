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

import dev.enola.common.convert.ObjectToStringBiConverters;
import dev.enola.data.iri.IRIConverter;

import java.util.UUID;

@Immutable
public final class UUID_IRI extends IDIRI<UUID> {
    // TODO /* non-public! */

    static final ConverterX<UUID_IRI, UUID> CONVERTER =
            new ConverterX<>("urn:uuid:", ObjectToStringBiConverters.UUID) {
                @Override
                protected UUID_IRI create(UUID id) {
                    return new UUID_IRI(id);
                }
            };

    // TODO Remove public
    public UUID_IRI(UUID uuid) {
        super(uuid);
    }

    public UUID_IRI() {
        // See also dev.enola.common.ByteSeq#random()
        super(UUID.randomUUID());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected IRIConverter<UUID_IRI> iriConverter() {
        return CONVERTER;
    }

    @Override
    protected boolean isComparableTo(Object other) {
        return other instanceof UUID_IRI;
    }
}
