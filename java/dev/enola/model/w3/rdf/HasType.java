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
package dev.enola.model.w3.rdf;

import com.google.common.reflect.TypeToken;

import dev.enola.model.w3.rdfs.Class;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingProvider;

import java.util.Set;

public interface HasType extends Thing {

    default Iterable<String> typesIRI() {
        return getOptional(KIRI.RDF.TYPE, new TypeToken<Iterable<String>>() {}).orElse(Set.of());
    }

    default Iterable<Class> types() {
        // return getOptional(KIRI.RDF.TYPE, new TypeToken<Iterable<Class>>() {}).orElse(Set.of());
        return ThingProvider.CTX.get(typesIRI(), Class.class);
    }

    // @IRI(KIRI.RDF.TYPE)
    default Class type() {
        return types().iterator().next();
    }

    interface Builder<B extends HasType> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        default HasType.Builder<B> type(String typeIRI) {
            set(KIRI.RDF.TYPE, typeIRI);
            return this;
        }
    }
}
