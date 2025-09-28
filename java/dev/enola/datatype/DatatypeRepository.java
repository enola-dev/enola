/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import dev.enola.common.context.TLC;
import dev.enola.data.Repository;

import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;

/**
 * {@link Repository} of {@link Datatype}.
 *
 * <p>Applications may have an implementation of this which permits "dynamic" datatypes.
 *
 * <p>Tests might want to use the fixed {@code dev.enola.model.Datatypes#DTR}.
 */
public interface DatatypeRepository extends Repository<Datatype<?>> {

    Optional<Datatype<?>> match(String text);

    /** DatatypeRepository which is always empty. */
    DatatypeRepository EMPTY =
            new DatatypeRepository() {
                @Override
                public Optional<Datatype<?>> match(String text) {
                    return Optional.empty();
                }

                @Override
                public Iterable<String> listIRI() {
                    return Collections.emptySet();
                }

                @Override
                public @Nullable Datatype<Object> get(String iri) {
                    return null;
                }
            };

    /** DatatypeRepository from {@link TLC}. */
    DatatypeRepository CTX =
            new DatatypeRepository() {
                @Override
                public Optional<Datatype<?>> match(String text) {
                    return TLC.get(DatatypeRepository.class).match(text);
                }

                @Override
                public Iterable<String> listIRI() {
                    return TLC.get(DatatypeRepository.class).listIRI();
                }

                @Override
                public @Nullable Datatype<?> get(String iri) {
                    return TLC.optional(DatatypeRepository.class)
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "Need DatatypeRepository in TLC for: " + iri))
                            .get(iri);
                }
            };
}
