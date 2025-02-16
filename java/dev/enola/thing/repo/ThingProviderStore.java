/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import dev.enola.data.ProviderStore;
import dev.enola.data.Repository;
import dev.enola.data.Store;
import dev.enola.thing.Thing;

/**
 * ProviderStore is both a {@link ThingProvider} as well as a {@link Store}.
 *
 * <p>It's however not (necessarily) also a {@link Repository}.
 */
public interface ThingProviderStore
        extends ThingProvider, ThingStore, ProviderStore<Thing, ThingStore> {}
