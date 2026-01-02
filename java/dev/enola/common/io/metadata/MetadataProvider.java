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
package dev.enola.common.io.metadata;

import org.jspecify.annotations.Nullable;

/** Provider of {@link Metadata}, given an IRI. */
public interface MetadataProvider<T> /* extends ProviderFromIRI<Metadata> */ {

    // TODO Simplify this?

    // @Override
    Metadata get(String iri);

    Metadata get(T object);

    // TODO Get rid of this variant?
    Metadata get(@Nullable T object, String iri);
}
