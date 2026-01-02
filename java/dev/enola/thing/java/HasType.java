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
package dev.enola.thing.java;

import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.Thing;

public interface HasType extends Thing {

    // TODO Remove this, and instead use KIRI.RDF.TYPE inline instead,
    //   because having this here as-is is visible EVERYWHERE, which is confusing;
    //   UNLESS it's repeated (and hides this) in every single static Thing interface.
    String IRI = KIRI.RDF.TYPE;

    default Iterable<Object> typesIRIs() {
        return getLinks(IRI);
    }

    interface Builder<B extends Thing> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        default Builder<B> addType(String typeIRI) {
            add(IRI, new Link(typeIRI));
            return this;
        }
    }
}
