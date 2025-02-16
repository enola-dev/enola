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
package dev.enola.model.w3.rdf;

import com.google.common.collect.ImmutableList;

import dev.enola.model.w3.rdfs.Class;
import dev.enola.model.w3.rdfs.HasClassIRI;
import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.AlwaysThingProvider;

import java.util.Optional;

public interface HasType extends Thing {
    // TODO Move HasType from dev.enola.model.w3.rdf to dev.enola.thing.java.meta?
    // Or, perhaps better, keep this here, and have a supertype there? Confusing?

    // TODO Actually, returns Iterable<Object> instead of String - make consistent!
    default Iterable<String> typesIRIs() {
        // TODO Create constant TypeTokens.ITERABLE_STRING
        return getLinks(KIRI.RDF.TYPE).stream().map(Object::toString).toList();
    }

    default Iterable<Class> types() {
        // return getOptional(KIRI.RDF.TYPE, new TypeToken<Iterable<Class>>() {}).orElse(Set.of());
        return AlwaysThingProvider.CTX.get(typesIRIs(), Class.class);
    }

    // TODO default KIRI.RDF.TYPE
    default Optional<Class> type() {
        var iterator = types().iterator();
        if (iterator.hasNext()) return Optional.of(iterator.next());
        else return Optional.empty();
    }

    interface Builder<B extends HasType> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        default HasType.Builder<B> addType(String typeIRI) {
            // TODO This is an ugly hack and needs fundamental review...
            //   just like Class.Builder.addRdfsClassProperty - same problem there...
            set(KIRI.RDF.TYPE, ImmutableList.of(new Link(typeIRI)));
            return this;
        }

        default HasType.Builder<B> addType(HasClassIRI typeIRI) {
            return addType(typeIRI.iri());
        }
    }
}
