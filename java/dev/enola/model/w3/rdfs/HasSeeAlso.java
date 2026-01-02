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
package dev.enola.model.w3.rdfs;

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

import java.util.Set;

public interface HasSeeAlso extends Thing {

    default @Nullable Set<String> seeAlso() {
        return get(IRI.Predicate.seeAlso, Set.class);
    }

    interface Builder<B extends HasSeeAlso> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        default HasSeeAlso.Builder<B> addSeeAlso(String seeAlso) {
            add(IRI.Predicate.seeAlso, seeAlso);
            return this;
        }

        default HasSeeAlso.Builder<B> addAllSeeAlso(Iterable<String> seeAlso) {
            addAll(IRI.Predicate.seeAlso, seeAlso, KIRI.SCHEMA.URL_DATATYPE);
            return this;
        }
    }
}
