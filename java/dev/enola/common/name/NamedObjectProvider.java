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
package dev.enola.common.name;

import java.util.Optional;

/** Provides read-only access to named objects, scoped by their class. */
public interface NamedObjectProvider {

    Iterable<String> names(Class<?> clazz);

    <T> Optional<T> opt(String name, Class<T> clazz);

    default <T> T get(String name, Class<T> clazz, Object context) throws IllegalArgumentException {
        return opt(name, clazz)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        name
                                                + " is needed in "
                                                + context
                                                + ", but not available, only: "
                                                + names(clazz)));
    }
}
