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
package dev.enola.datatype;

import dev.enola.data.Provider;

import org.jspecify.annotations.Nullable;

interface ProviderX extends Provider<String, Object> { // NOT ProviderFromIRI<V>

    @Nullable Object get(String name, String datatypeIRI);

    @Nullable <X> X get(String name, Datatype<X> datatype);

    @Nullable <X> X get(String name, Class<X> clazz);

    /**
     * Because this has no explicit context, it may make an assumption about what you could have
     * meant, e.g. for something URI like an implementation may assume IRI of Thing instead of URL
     * of Resource ; it's thus generally better to use the other methods.
     */
    @Override
    @Nullable Object get(String name);
}
