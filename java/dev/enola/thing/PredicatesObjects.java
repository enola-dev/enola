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

import com.google.common.reflect.TypeToken;
import com.google.errorprone.annotations.ImmutableTypeParameter;

import dev.enola.common.convert.ConversionException;
import dev.enola.thing.repo.ThingProvider;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.*;

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

    // boolean isLink(String predicateIRI) is intentionally not available; use #getLinks() instead!

    default boolean isLinkObject(@Nullable Object object) {
        return object instanceof URI || object instanceof Link;
    }

    default boolean isIterable(String predicateIRI) {
        var value = get(predicateIRI);
        return value instanceof Iterable;
    }

    default boolean isOrdered(String predicateIRI) {
        var value = get(predicateIRI);
        return value instanceof List;
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

    default @Nullable String datatype(HasPredicateIRI predicateIRI) {
        return datatype(predicateIRI.iri());
    }

    @Deprecated // TODO Is this really useful? In which use case scenario? Remove...
    Map<String, String> datatypes();

    /**
     * Object of predicate. The type is e.g. directly a String, Integer etc. Alternatively, it may
     * be a {@link Link} (or {@link URI}) with an IRI or another PredicatesObjects (for an "inline
     * embedded/expanded blank node") or a {@link java.util.List} of such items. The object is
     * immutable. May be null if Thing has no such predicate.
     *
     * @deprecated Use {@link #get(String, Class)} instead.
     */
    @Deprecated // TODO Remove after replacing all usages with #get(String, aClass | Object.class)
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

    @SuppressWarnings("unchecked")
    default <T> @Nullable T get(String predicateIRI, TypeToken<T> typeToken) {
        return (T) get(predicateIRI, typeToken.getRawType());
    }

    @SuppressWarnings("unchecked")
    default <T> Optional<T> getOptional(String predicateIRI, TypeToken<T> typeToken) {
        return (Optional<T>) getOptional(predicateIRI, typeToken.getRawType());
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
        return ObjectConversions.as(object, klass, this, predicateIRI);
    }

    default @Nullable String getString(String predicateIRI) {
        return getOptional(predicateIRI, String.class).orElse(null);
    }

    default <T> Optional<T> getOptional(HasPredicateIRI predicateIRI, Class<T> klass) {
        return getOptional(predicateIRI.iri(), klass);
    }

    default <T> Optional<T> getOptional(HasPredicateIRI predicateIRI, TypeToken<T> typeToken) {
        return getOptional(predicateIRI.iri(), typeToken);
    }

    default @Nullable String getString(HasPredicateIRI predicateIRI) {
        return getString(predicateIRI.iri());
    }

    default <T extends Thing> Optional<T> getThing(String predicateIRI, Class<T> klass) {
        return getOptional(predicateIRI, String.class)
                .map(linkIRI -> ThingProvider.CTX.get(linkIRI, klass));
    }

    @SuppressWarnings("unchecked")
    default <T extends Thing> Iterable<T> getThings(String predicateIRI, Class<T> klass) {
        var iris = getOptional(predicateIRI, Iterable.class).orElse(Set.of());
        return ThingProvider.CTX.get(iris, klass);
    }

    default <T extends Thing> Iterable<T> getThings(HasPredicateIRI predicateIRI, Class<T> klass) {
        return getThings(predicateIRI.iri(), klass);
    }

    /**
     * Links, for predicate.
     *
     * <p>This transparently correctly handles if said predicate is not a link, a single link, or an
     * iterable of which some elements are links.
     *
     * @return iterable objects which are links. Never null, but may well be empty. The type of the
     *     contained elements can vary, and be either {@link String}, {@link URI}, {@link Link};
     *     it's possible to use {@link Object#toString()} to get the IRI of the link, because {@link
     *     Link#toString()} is implemented as expected.
     */
    // TODO ifStruct(), should it recursive into finding links in nested PredicatesObjects?
    @SuppressWarnings("unchecked")
    default Collection<Object> getLinks(String predicateIRI) {
        if (!isIterable(predicateIRI)) {
            return getOptional(predicateIRI, Object.class)
                    .filter(this::isLinkObject)
                    .map(Set::of)
                    .orElse(Set.of());
        } else
            return get(predicateIRI, Collection.class).stream().filter(this::isLinkObject).toList();
    }

    // TODO get... other types.

    @Deprecated
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

    // TODO How to best name this, and the equivalent in Thing?
    interface Builder2<B extends PredicatesObjects> extends PredicatesObjects.Builder<B> {

        /**
         * Adds one of possibly several value objects for the given predicate IRI.
         *
         * <p>This is UNORDERED! Insertion order may NOT be preserved. Duplicates are not allowed
         * and will cause an error (possibly only on {@link #build()}). It is an error if this
         * property has already been set to anything else than a {@link Set}.
         */
        <@ImmutableTypeParameter T> PredicatesObjects.Builder2<B> add(String predicateIRI, T value);

        <@ImmutableTypeParameter T> PredicatesObjects.Builder2<B> add(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        /**
         * Adds one of possibly several value objects for the given predicate IRI - and preserves
         * order.
         *
         * <p>Duplicates ARE allowed. It is an error if this property has already been set to
         * anything else than a {@link List}.
         */
        <@ImmutableTypeParameter T> PredicatesObjects.Builder2<B> addOrdered(
                String predicateIRI, T value);

        <@ImmutableTypeParameter T> PredicatesObjects.Builder2<B> addOrdered(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        @Override
        B build();
    }
}
