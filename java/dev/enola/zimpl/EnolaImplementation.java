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
package dev.enola.zimpl;

import dev.enola.Action;
import dev.enola.Enola;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.data.ProviderFromIRI;
import dev.enola.thing.Thing;

import java.net.URI;

public class EnolaImplementation implements Enola {

    private final ProviderFromIRI<?> provider;

    public EnolaImplementation() {
        this(new EmptyResource.Provider());
    }

    private EnolaImplementation(ProviderFromIRI<?> provider) {
        this.provider = provider;
    }

    @Override
    public Object act(String objectIRI, String verbIRI) {
        // TODO Look-up and invoke Action, instead of hard-coding Get...
        return provider.get(objectIRI);
    }

    @Override
    public Object act(URI objectURI, Thing action) {
        // TODO Look-up and invoke Action, instead of hard-coding Get...
        // TODO ProviderFromIRI must have get(URI) ...
        return provider.get(objectURI.toString());
    }

    @Override
    public Object act(Thing object, Action action) {
        return null; // TODO Implement... class IriThing implements Thing, has URI?
    }
}
