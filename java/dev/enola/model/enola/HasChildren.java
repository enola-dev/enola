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
package dev.enola.model.enola;

import dev.enola.data.iri.IRI;
import dev.enola.thing.Link;
import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

import java.util.Set;

public interface HasChildren extends Thing {

    // TODO Set<T> children()

    default @Nullable Set<Link> childrenIRI() {
        return get("https://enola.dev/children", Set.class);
    }

    interface Builder<B extends HasChildren> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        default HasChildren.Builder<B> addChildIRI(IRI childIRI) {
            add("https://enola.dev/children", childIRI);
            return this;
        }
    }
}
