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
package dev.enola.thing.java;

import com.google.common.collect.ImmutableList;

import dev.enola.thing.Thing;

public class TBFChain implements TBF {

    private final ImmutableList<TBF> chain;

    public TBFChain(ImmutableList<TBF> chain) {
        this.chain = chain;
    }

    @Override
    public Thing.Builder<Thing> create(String typeIRI) {
        for (TBF tbf : chain) {
            if (tbf.handles(typeIRI)) return tbf.create(typeIRI);
        }
        throw new IllegalStateException("No registered TBF handles: " + typeIRI);
    }

    @Override
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface) {
        for (TBF tbf : chain) {
            if (tbf.handles(builderInterface)) return tbf.create(builderInterface, thingInterface);
        }
        throw new IllegalStateException("No registered TBF handles: " + builderInterface);
    }

    @Override
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface, int expectedSize) {
        for (TBF tbf : chain) {
            if (tbf.handles(builderInterface))
                return tbf.create(builderInterface, thingInterface, expectedSize);
        }
        throw new IllegalStateException("No registered TBF handles: " + builderInterface);
    }

    @Override
    public boolean handles(String typeIRI) {
        return true;
    }
}
