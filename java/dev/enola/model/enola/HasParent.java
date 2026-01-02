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

import dev.enola.thing.Link;
import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

public interface HasParent extends Thing {

    // TODO @Nullable T parent()

    default @Nullable String parentIRI() {
        return getString("https://enola.dev/parent");
    }

    interface Builder<B extends HasParent> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        default HasParent.Builder<B> parentIRI(String iri) {
            set("https://enola.dev/parent", new Link(iri));
            return this;
        }
    }
}
