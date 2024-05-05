/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.thing;

/**
 * PredicatesObjects is a "struct" of Predicates and their Objects.
 *
 * <p>Contrary to a {@link Thing}, this has no "identity" and thus no IRI. RDF calls this a "Blank
 * Node". This is informally often also referred to as a Things's Properties - it's just the Thing
 * without its identifying IRI.
 *
 * <p>It is typically used for objects "contained inside" Things (or their nested sub-structs).
 */
public interface PredicatesObjects {}
