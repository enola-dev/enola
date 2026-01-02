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
package dev.enola.data.iri;

import org.jspecify.annotations.Nullable;

public abstract class StringableIRI extends IRI {
    // TODO Rename this... all IRI are "stringable" - this is... StringCacheingIRI?

    @SuppressWarnings("Immutable") // We (hopefully) know what we're doing!
    private transient @Nullable String iri;

    @Override
    public final String toString() {
        if (iri == null) iri = createStringIRI();
        return iri;
    }

    protected abstract String createStringIRI();

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (isEqualTo(other)) return true;
        if (other instanceof IRI otherIRI) return this.toString().equals(otherIRI.toString());
        return false;
    }

    protected abstract boolean isEqualTo(Object other);

    @Override
    public final int compareTo(IRI other) {
        if (isComparableTo(other)) return compare(other);
        else return this.toString().compareTo(other.toString());
    }

    protected abstract boolean isComparableTo(Object other);

    protected abstract int compare(Object other);
}
