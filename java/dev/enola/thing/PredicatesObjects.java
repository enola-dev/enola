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

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.DatatypeRepository;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    @Deprecated // TODO Re-think API... I would like to avoid exposing Object - remove this?
    Map<String, Object> properties();

    /** IRIs of the Predicates of this Thing. */
    // TODO Reconsider if this method is really required? Why not just #properties().keySet()?
    Set<String> predicateIRIs();

    // TODO These is*() methods could be replaced with a Visitor - but how-to for nested Structs?!
    // Look into e.g. how EMF does Visitors...

    default boolean isLink(String predicateIRI) {
        var value = get(predicateIRI);
        return value instanceof URI || value instanceof Link;
    }

    default boolean isIterable(String predicateIRI) {
        var value = get(predicateIRI);
        return value instanceof Iterable;
    }

    default boolean isStruct(String predicateIRI) {
        var value = get(predicateIRI);
        return value instanceof PredicatesObjects;
    }

    // TODO isLangString(String predicateIRI) needed? How would it be used?

    // NB: isLiteral(String predicateIRI) is not needed; Datatype conversion handles that.

    /**
     * IRI of datatype of predicate, if any (else null). Not all predicates will have a datatype
     * set. This is required because the predicate's Object Java class is not necessarily unique;
     * e.g. both dev.enola.model.schema.Datatypes.DATE as well as dev.enola.model.xsd.DATE are both
     * java.time.LocalDate instances.
     */
    @Nullable String datatype(String predicateIRI);

    @Deprecated // TODO Is this really useful? In which use case scenario? Remove...
    Map<String, String> datatypes();

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
     *
     * @deprecated Use {@link #get(String, Class)} instead.
     */
    @Deprecated // TODO Remove after replacing all usages with #get(String, Class)
    <T> @Nullable T get(String predicateIRI);

    /**
     * Object of predicate, with type conversion - or failure.
     *
     * <p>BEWARE: This may well fail and throw an <tt>IllegalArgumentException</tt>! You can never
     * really be sure what Java type an object of a predicate is. If in doubt, use {@link
     * #getOptional(String, Class)}, or perhaps {@link #get(String)} with an {@link
     * dev.enola.common.convert.ObjectClassConverter}.
     */
    default <T> @Nullable T get(String predicateIRI, Class<T> klass) {
        return getOptional(predicateIRI, klass).orElse(null);
    }

    /**
     * Object of predicate, with type conversion - as Optional.
     *
     * @return property as type, if set
     * @throws IllegalStateException if Datatype is not found, but needed
     * @throws ConversionException if known Datatype failed to convert
     */
    @SuppressWarnings("unchecked")
    default <T> Optional<T> getOptional(String predicateIRI, Class<T> klass) {
        Object object = get(predicateIRI);
        if (object == null) return Optional.empty();
        if (klass.isInstance(object)) return Optional.of((T) object);
        if (String.class.equals(klass)) {
            if (object instanceof Literal literal) return Optional.of((T) literal.value());
            if (object instanceof URI uri) return Optional.of((T) uri.toString());
            if (object instanceof Link link) return Optional.of((T) link.iri());
            // TODO Ideally, it should look up the "right" text, using a Lang Ctx Key from the TLC
            if (object instanceof LangString langString) return Optional.of((T) langString.text());
        }
        try {
            var dtIRI = datatypeLEGACY(predicateIRI);
            // TODO Find Datatype via object Java class lookup in DatatypeRepository?
            if (dtIRI == null)
                throw new IllegalStateException(
                        predicateIRI
                                + " has no Datatype; cannot convert "
                                + object
                                + " of "
                                + object.getClass()
                                + " to "
                                + klass);
            var dtr = TLC.get(DatatypeRepository.class);
            var dt = dtr.get(dtIRI);
            if (dt == null)
                throw new IllegalStateException(
                        dtIRI
                                + " not found; cannot convert "
                                + object
                                + " of "
                                + object.getClass()
                                + " to "
                                + klass);
            return dt.stringConverter().convertToType(object, klass);

        } catch (IOException e) {
            // TODO Get rid of throws IOException and remove this.
            // Or better log any exceptions and return just Optional.empty()?
            throw new ConversionException("Failed to convert " + object + " to " + klass, e);
        }
    }

    default @Nullable String getString(String predicateIRI) {
        return getOptional(predicateIRI, String.class).orElse(null);
    }

    // TODO get... other types.

    Builder<? extends PredicatesObjects> copy();

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    interface Builder<B extends PredicatesObjects> // skipcq: JAVA-E0169
            extends dev.enola.common.Builder<B> {

        <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> set(String predicateIRI, T value);

        <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> set(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        @Override
        B build();
    }
}
