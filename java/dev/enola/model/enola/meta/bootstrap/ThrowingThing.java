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
package dev.enola.model.enola.meta.bootstrap;

import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public abstract class ThrowingThing implements Thing, Thing.Builder {

    @Override
    public Map<String, Object> properties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> predicateIRIs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable String datatype(String predicateIRI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> datatypes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> @Nullable T get(String predicateIRI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Builder<? extends Thing> copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Thing.Builder set(String predicateIRI, Object value) {
        return null;
    }

    @Override
    public Thing.Builder set(String predicateIRI, Object value, String datatypeIRI) {
        return null;
    }
}
