/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object;

import java.util.Optional;

/** Provides {@link Identifiable} objects, given their ID <b>and</b> Class. */
public interface ProviderFromID {

    // TODO Just use (retrofit) the [new] NamedObjectProvider instead!

    <T extends Identifiable> Optional<T> opt(String id, Class<T> clazz);

    default <T extends Identifiable> T get(String id, Class<T> clazz)
            throws IllegalArgumentException {
        return opt(id, clazz)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No " + clazz.getName() + " object with ID " + id));
    }
}
