/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

/**
 * Queryable returns Results (R), given a Query (Q).
 *
 * @param <Q> is a {@link Query} implementation.
 * @param <R> is typically an Iterable or Stream or something like that.
 */
// TODO This API will likely need some more thought in the future, and evolve when implemented
public interface Queryable<Q extends Query, R> {

    // TODO Merge this with Repository, to simplify overly complex public API surface

    R query(Q query); // TODO throws Data[Query?]Exception

    // TODO ? List<QL> queryLanguages();
}
