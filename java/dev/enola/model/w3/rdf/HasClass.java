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
package dev.enola.model.w3.rdf;

import dev.enola.model.w3.rdfs.Class;
import dev.enola.model.w3.rdfs.HasClassIRI;
import dev.enola.thing.java.HasType;
import dev.enola.thing.repo.AlwaysThingProvider;

public interface HasClass extends HasType {

    default Iterable<Class> types() {
        return AlwaysThingProvider.CTX.get(typesIRIs(), Class.class, Class.Builder.class);
    }

    interface Builder<B extends HasClass> extends HasType.Builder<B> { // skipcq: JAVA-E0169
        default HasClass.Builder<B> addType(HasClassIRI typeIRI) {
            addType(typeIRI.iri());
            return this;
        }
    }
}
