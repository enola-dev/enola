/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import com.google.common.collect.ImmutableList;

import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.Thing;

public interface HasType extends Thing {

    default Iterable<Object> typesIRIs() {
        return getLinks(KIRI.RDF.TYPE);
    }

    interface Builder<B extends Thing> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        default Builder<B> addType(String typeIRI) {
            // TODO This is an ugly hack and needs fundamental review...
            //   just like Class.Builder.addRdfsClassProperty - same problem there...
            set(KIRI.RDF.TYPE, ImmutableList.of(new Link(typeIRI)));
            return this;
        }
    }
}
