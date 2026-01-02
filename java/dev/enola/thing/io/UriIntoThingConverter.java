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
package dev.enola.thing.io;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConverterInto;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingRepositoryStore;

import java.net.URI;

public interface UriIntoThingConverter extends ConverterInto<URI, ThingRepositoryStore> {

    default void addOrigin(URI uri, Thing.Builder<?> thingBuilder) {
        // This is "cool", but "very ugly and overwhelming"
        // e.g. on graph visualizations, so conditionally disable it:
        if (TLC.optional(UriIntoThingConverters.Flags.ORIGIN).orElse(true))
            thingBuilder.add(KIRI.E.ORIGIN, uri);
    }
}
