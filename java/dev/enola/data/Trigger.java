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
package dev.enola.data;

import org.jspecify.annotations.Nullable;

/**
 * Trigger for updates to {@link Store}.
 *
 * <p>Trigger implementations themselves should be written as simple single-threaded code. Callers
 * may or may not invoke different triggers in parallel (but that is something that the caller
 * handles, not the implementations of individual triggers).
 *
 * @param <T> the type of objects in the store
 */
public interface Trigger<T> {

    // TODO Scripting hooks, see https://github.com/enola-dev/enola/issues/1105

    void updated(@Nullable T existing, T update);

    boolean handles(Object object);
}
