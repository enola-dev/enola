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
package dev.enola.thing.repo;

import dev.enola.thing.Thing;

import java.util.stream.Stream;

public interface ThingsProvider {

    // TODO Rethink this... this doesn't make sense, there should only ever be 1 Thing per IRI?!
    // KIRI.E.LIST_THINGS & KIRI.E.LIST_IRIS just needs to return a Thing with 1 list property...
    // Switch callers to existing old ThingProvider (NB singular Thing, not ThingsProvider).

    Stream<Thing> getThings(String iri);
}
