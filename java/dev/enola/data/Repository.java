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
package dev.enola.data;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

import java.util.stream.Stream;

/**
 * Repository is a Provider which, in addition to being able to getting a single T given an IRI, can
 * also "list" all such IRIs which it "has".
 *
 * <p>This is not quite a real full {@link Queryable}, but kind of like a subset of it, because it's
 * "queryable" for "everything" (without any "query", really).
 */
public interface Repository<T> extends ProviderFromIRI<T> {

    // TODO Merge this with Queryable, to simplify overly complex public API surface

    // TODO Switch from Iterable to Stream?
    Iterable<String> listIRI();

    /**
     * list() returns Ts directly (not just the IRIs, like {@link #listIRI()}).
     *
     * <p>This default implementation here just combines {@link #listIRI()} and {@link
     * #get(String)}. Your subclass may be able to provide a more efficient and more "direct"
     * implementation?
     */
    default Iterable<T> list() {
        return Iterables.transform(listIRI(), this::get);
    }

    default Stream<T> stream() {
        return Streams.stream(list());
    }
}
