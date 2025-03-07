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
package dev.enola.data.id;

import static java.util.Objects.requireNonNull;

import com.google.errorprone.annotations.Immutable;

import dev.enola.data.iri.StringableIRI;

import java.io.IOException;

@Immutable(containerOf = "T")
/** ID-IRI is an {@link IRI} based on an ID object. */
public abstract class IDIRI<T extends Comparable<T>> extends StringableIRI {

    // TODO Add URL Pattern, in & out!

    private final T id;

    protected IDIRI(T id) {
        this.id = requireNonNull(id);
    }

    protected abstract IdConverter<T> idConverter();

    @Override
    public void append(Appendable appendable) throws IOException {
        if (!idConverter().convertInto(id, appendable))
            throw new IllegalArgumentException(id.toString());
    }

    @Override
    protected String createStringIRI() {
        return idConverter().convertTo(id);
    }

    @Override
    protected boolean isEqualTo(Object other) {
        return id.equals(other);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    protected boolean isComparableTo(Object other) {
        return idConverter().idClass().isInstance(other);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int compare(Object other) {
        return id.compareTo((T) other);
    }
}
