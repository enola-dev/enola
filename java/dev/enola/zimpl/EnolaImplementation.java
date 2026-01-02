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
package dev.enola.zimpl;

import dev.enola.Action;
import dev.enola.Enola;
import dev.enola.common.convert.ObjectWithTypeTokenConverter;
import dev.enola.data.Repository;
import dev.enola.thing.Thing;

import java.net.URI;

public class EnolaImplementation implements Enola {

    private final ObjectWithTypeTokenConverter converter;
    private final Repository<Action<?, ?>> actions;

    public EnolaImplementation(
            Repository<Action<?, ?>> actions, ObjectWithTypeTokenConverter converter) {
        this.actions = actions;
        this.converter = converter;
    }

    @Override
    public Object act(URI objectURI, Thing actionThing) {
        return act(objectURI.toString(), actionThing.iri());
    }

    @Override
    public Object act(String objectIRI, Thing actionThing) {
        return act(objectIRI, actionThing.iri());
    }

    @Override
    public Object act(String objectIRI, String actionIRI) {
        var action = actions.get(actionIRI);
        var convertedObject = converter.convertToTypeOrThrow(objectIRI, action.argumentType());
        return action.act(convertedObject.as(action.argumentType()));
    }
}
