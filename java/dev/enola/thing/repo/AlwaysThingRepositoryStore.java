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
package dev.enola.thing.repo;

import dev.enola.data.Store;
import dev.enola.thing.Thing;

import java.util.stream.Stream;

public class AlwaysThingRepositoryStore extends AlwaysThingProvider
        implements ThingRepositoryStore {

    private final ThingRepositoryStore delegateRepositoryStore;

    public AlwaysThingRepositoryStore(ThingRepositoryStore delegate) {
        super(delegate);
        this.delegateRepositoryStore = delegate;
    }

    @Override
    public Iterable<String> listIRI() {
        return delegateRepositoryStore.listIRI();
    }

    @Override
    public Iterable<Thing> list() {
        return delegateRepositoryStore.list();
    }

    @Override
    public Stream<Thing> stream() {
        return delegateRepositoryStore.stream();
    }

    @Override
    public Store<Thing> store(Thing item) {
        return delegateRepositoryStore.store(item);
    }

    @Override
    public Store<Thing> storeAll(Iterable<Thing> items) {
        return delegateRepositoryStore.storeAll(items);
    }
}
