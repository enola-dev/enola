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
package dev.enola.thing;

import com.google.common.collect.ImmutableMap;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/** {@link Thing} with only an IRI and no properties (optimized). */
public class OnlyIRIThing implements IImmutableThing {
    private final String iri;

    public OnlyIRIThing(String iri) {
        this.iri = iri;
    }

    @Override
    public String iri() {
        return iri;
    }

    @Override
    public ImmutableMap<String, Object> properties() {
        return ImmutableMap.of();
    }

    @Override
    public Collection<String> predicateIRIs() {
        return List.of();
    }

    @Override
    public @Nullable String datatype(String predicateIRI) {
        return null;
    }

    @Override
    public <T> @Nullable T get(String predicateIRI) {
        return null;
    }

    @Override
    public Thing.Builder<Thing> copy() {
        throw new UnsupportedOperationException("TODO");
    }
}
