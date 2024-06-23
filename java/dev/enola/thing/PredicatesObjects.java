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

import com.google.errorprone.annotations.ImmutableTypeParameter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * PredicatesObjects is a "struct" of Predicates and their Objects.
 *
 * <p>Contrary to a {@link Thing}, this has no "identity" and thus no IRI. RDF calls this a "Blank
 * Node". This is informally often also referred to as a Things's Properties - it's just the Thing
 * without its identifying IRI.
 *
 * <p>It is typically used for objects "contained inside" Things (or their nested sub-structs).
 */
public interface PredicatesObjects {

    /**
     * The Map's key is the IRI of a predicate, and the value is as would be returned by {@link
     * #get(String)}.
     */
    Map<String, Object> properties();

    /** IRIs of the Predicates of this Thing. */
    Collection<String> predicateIRIs();

    /**
     * IRI of datatype of predicate, if any (else null). Not all predicates will have a datatype
     * set. This is required because the predicate's Object Java class is not necessarily unique;
     * e.g. both dev.enola.model.schema.Datatypes.DATE as well as dev.enola.model.xsd.DATE are both
     * java.time.LocalDate instances.
     */
    @Nullable String datatype(String predicateIRI);

    @Deprecated // TODO Remove once record Literal is gone
    default @Nullable String datatypeLEGACY(String predicateIRI) {
        var datatype = datatype(predicateIRI);
        if (datatype != null) return datatype;

        var object = get(predicateIRI);
        if (object != null && object instanceof Literal literal) {
            return literal.datatypeIRI();
        }

        return null;
    }

    /**
     * Object of predicate. The type is e.g. directly a String, Integer etc. Alternatively, it may
     * be a {@link Link} (or {@link URI}) with an IRI or another PredicatesObjects (for an "inline
     * embedded/expanded blank node") or a {@link java.util.List} of such items. The object is
     * immutable. May be null if Thing has no such predicate.
     */
    <T> @Nullable T get(String predicateIRI);

    /**
     * Object of predicate, with type conversion - or failure.
     *
     * <p>BEWARE: This may well fail and throw an <tt>IllegalArgumentException</tt>! You can never
     * really be sure what Java type an object of a predicate is. If in doubt, use {@link
     * #getOptional(String, Class)}, or perhaps {@link #get(String)} with an {@link
     * dev.enola.common.convert.ObjectClassConverter}.
     */
    default <T> T get(String predicateIRI, Class<T> klass) {
        return getOptional(predicateIRI, klass).orElse(null);
    }

    /** Object of predicate, with conversion - as Optional (never fails). */
    @SuppressWarnings("unchecked")
    default <T> Optional<T> getOptional(String predicateIRI, Class<T> klass) {
        Object object = get(predicateIRI);
        if (object == null) return Optional.empty();
        if (klass.isInstance(object)) return Optional.of((T) object);
        try {
            // TODO Conversion should use Datatype... but how to look one up?! GenJavaThing has..
            // NB: Do NOT e.g. throw new IllegalArgumentException if conversion !isPresent()
            return ThingObjectClassConverter.INSTANCE.convertToType(object, klass);
        } catch (IOException e) {
            // TODO Get rid of throws IOException and remove this.
            // Or better log any exceptions and return just Optional.empty()?
            throw new UncheckedIOException("Failed to convert " + object + " to " + klass, e);
        }
    }

    default @Nullable String getString(String predicateIRI) {
        return getOptional(predicateIRI, String.class).orElse(null);
    }

    // TODO get... other types.

    Builder<? extends PredicatesObjects> copy();

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    interface Builder<B extends PredicatesObjects> // skipcq: JAVA-E0169
            extends dev.enola.common.Builder<PredicatesObjects> {

        <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> set(String predicateIRI, T value);

        <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> set(
                String predicateIRI, T value, String datatypeIRI);

        @Override
        B build();
    }
}
