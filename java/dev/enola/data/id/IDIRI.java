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
package dev.enola.data.id;

import static java.util.Objects.requireNonNull;

import com.google.errorprone.annotations.Immutable;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ObjectToStringBiConverter;
import dev.enola.data.iri.IRIConverter;
import dev.enola.data.iri.StringableIRI;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

@Immutable(containerOf = "T")
/** ID-IRI is an {@link IRI} based on an ID object. */
// skipcq: JAVA-W0100
public abstract class IDIRI<T extends Comparable<T>> extends StringableIRI {
    // TODO dev.enola.data.id.IDIRI to dev.enola.data.iri

    // TODO Add URL Pattern, in & out!

    private final T id;

    protected IDIRI(T id) {
        this.id = requireNonNull(id);
    }

    // TODO Consider "pulling up" an Object id() method to IRI itself?
    public T id() {
        return id;
    }

    protected abstract <C extends IDIRI<T>> IRIConverter<C> iriConverter();

    @Override
    public void append(Appendable appendable) throws IOException {
        if (!iriConverter().convertInto(this, appendable))
            throw new IllegalArgumentException(id.toString());
    }

    @Override
    protected String createStringIRI() {
        return iriConverter().convertTo(this);
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
    @SuppressWarnings("unchecked")
    protected int compare(Object other) {
        return id.compareTo(((IDIRI<T>) other).id);
    }

    protected abstract static class ConverterX<C extends IDIRI<T>, T extends Comparable<T>>
            implements IRIConverter<C> {
        private final String prefix;
        private final ObjectToStringBiConverter<T> converter;

        protected ConverterX(String prefix, ObjectToStringBiConverter<T> converter) {
            this.prefix = prefix;
            this.converter = converter;
        }

        protected abstract C create(T id);

        @Override
        public Optional<C> convert(String input) throws ConversionException {
            if (!input.startsWith(prefix)) return Optional.empty();
            @Nullable T id = converter.convertFrom(input.substring(prefix.length()));
            if (id == null)
                throw new ConversionException("Not a TestID: " + input); // Optional.empty()?
            return Optional.of(create(id));
        }

        @Override
        public boolean convertInto(C from, Appendable into)
                throws ConversionException, IOException {
            into.append(prefix);
            return converter.convertInto(from.id(), into);
        }
    }
}
