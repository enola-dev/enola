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
package dev.enola.thing.java;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.AbstractThing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
// skipcq: JAVA-W0100
public abstract class DelegatingThing extends AbstractThing implements Thing {

    private final Thing delegate;

    protected DelegatingThing(Thing delegate) {
        this.delegate = delegate;
    }

    @Override
    public String iri() {
        return delegate.iri();
    }

    @Override
    public Map<String, Object> properties() {
        return delegate.properties();
    }

    @Override
    public Set<String> predicateIRIs() {
        return delegate.predicateIRIs();
    }

    @Override
    public Map<String, String> datatypes() {
        return delegate.datatypes();
    }

    @Override
    @Nullable
    public String datatype(String predicateIRI) {
        return delegate.datatype(predicateIRI);
    }

    @Override
    public <T> @Nullable T get(String predicateIRI) {
        return delegate.get(predicateIRI);
    }

    @Override
    public <T> T get(String predicateIRI, Class<T> klass) {
        return delegate.get(predicateIRI, klass);
    }

    @Override
    public <T> Optional<T> getOptional(String predicateIRI, Class<T> klass) {
        return delegate.getOptional(predicateIRI, klass);
    }

    @Override
    @Nullable
    public String getString(String predicateIRI) {
        return delegate.getString(predicateIRI);
    }
}
