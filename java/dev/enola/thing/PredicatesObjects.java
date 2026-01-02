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
package dev.enola.thing;

import com.google.common.reflect.TypeToken;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
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
public interface PredicatesObjects /*<TT /*extends PredicatesObjects<?>>*/ {

    // TODO Write default implementation of as() with ProxyTBF...
    // default <U extends PredicatesObjects<TT>> U as(Class<U> clazz) {

    /**
     * The Map's key is the IRI of a predicate, and the value is as would be returned by {@link
     * #get(String)}.
     */
    @Deprecated // TODO Re-think API... I would like to avoid exposing Object - remove this?
    Map<String, Object> properties();

    /** IRIs of the Predicates of this Thing. */
    // TODO Reconsider if this method is really required? Why not just #properties().keySet()?
    // ^^^ it's useful to avoid loading a huge Thing entirely into memory...
    // TODO Use Iterable<> instead Set<>
    Set<String> predicateIRIs();

    // TODO These is*() methods could be replaced with a Visitor - but how-to for nested Structs?!
    // Look into e.g. how EMF does Visitors...

    // boolean isLink(String predicateIRI) is intentionally not available; use #getLinks() instead!

    default boolean isLinkObject(@Nullable Object object) {
        // TODO object instanceof HasIRI, instead of Link ?
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
     * e.g. both dev.enola.model.schema.Datatypes.DATE as well as dev.enola.model.xsd.DATE are
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
    @Deprecated // TODO Remove after adopting ThingVisitor
    //  Note bene: Cannot always just replace all usages with #get(String, aClass | Object.class);
    //    actually technically with Object.class we could; but the ThingVisitor is better!
    <T> @Nullable T get(String predicateIRI);

    /**
     * Object of predicate, with type conversion - or failure.
     *
     * <p>BEWARE: This may well fail and throw an <code>IllegalArgumentException</code>! You can
     * never really be sure what Java type an object of a predicate is. If in doubt, use {@link
     * #getOptional(String, Class)}, or perhaps {@link #get(String)} with an {@link
     * dev.enola.common.convert.ObjectClassConverter}.
     */
    @Deprecated // TODO Remove; create & replace callers with #get(String, Class, Class)
    default <T> @Nullable T get(String predicateIRI, Class<T> klass) {
        return getOptional(predicateIRI, klass).orElse(null);
    }

    @Deprecated // TODO Remove; create & replace callers with #get(HasPredicateIRI, Class, Class)
    default <T> @Nullable T get(HasPredicateIRI predicate, Class<T> klass) {
        return get(predicate.iri(), klass);
    }

    @SuppressWarnings("unchecked")
    default <T> @Nullable T get(String predicateIRI, TypeToken<T> typeToken) {
        return (T) get(predicateIRI, typeToken.getRawType());
    }

    /**
     * Object of predicate, with type conversion - as Optional.
     *
     * @return property as type, if set
     * @throws IllegalStateException if Datatype is not found, but needed
     * @throws ConversionException if known Datatype failed to convert
     */
    @SuppressWarnings("unchecked")
    // TODO Remove; create & replace callers with #getOptional(String, Class, Class)
    default <T> Optional<T> getOptional(String predicateIRI, Class<T> klass) {
        Object object = get(predicateIRI);
        return ObjectConversions.as(object, klass, this, predicateIRI);
    }

    default @Nullable String getString(String predicateIRI) {
        return getOptional(predicateIRI, String.class).orElse(null);
    }

    default @Nullable String getString(HasPredicateIRI predicate) {
        return getString(predicate.iri());
    }

    @Deprecated // TODO Replace all callers with new variant, see below
    default <T extends Thing> Optional<T> getThing(String predicateIRI, Class<T> klass) {
        return getOptional(predicateIRI, String.class)
                .map(linkIRI -> ThingProvider.CTX.get(linkIRI, klass));
    }

    // TODO Rename to getThing
    @Deprecated // TODO Replace all callers with new variant, see below
    default <T extends Thing> T getThingOrThrow(String predicateIRI, Class<T> klass) {
        var opt = getOptional(predicateIRI, String.class);
        if (!opt.isPresent())
            throw new IllegalStateException(this + " has no value for " + predicateIRI);
        var thingIRI = opt.get();
        T thing = ThingProvider.CTX.get(thingIRI, klass);
        if (thing == null) throw new IllegalStateException(thingIRI + " not found");
        return thing;
    }

    // TODO Rename to getThingOpt; or (better) remove Optional, when fully adopting Always*
    default <T extends Thing, B extends Thing.Builder<T>> Optional<T> getThing(
            String predicateIRI, Class<T> klass, Class<B> builderClass) {
        return getOptional(predicateIRI, String.class)
                .map(linkIRI -> ThingProvider.CTX.get(linkIRI, klass, builderClass));
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    // TODO Remove after creating and replacing all usages with #getThings(String, Class, Class)
    default <T extends Thing> Iterable<T> getThings(String predicateIRI, Class<T> klass) {
        var iris = getOptional(predicateIRI, Iterable.class).orElse(Set.of());
        return ThingProvider.CTX.get(iris, klass);
    }

    default <T extends Thing, B extends Thing.Builder<T>> Iterable<T> getThings(
            String predicateIRI, Class<T> klass, Class<B> builderClass) {
        var iris = getOptional(predicateIRI, Iterable.class).orElse(Set.of());
        return ThingProvider.CTX.get(iris, klass, builderClass);
    }

    default Iterable<Thing> getThings(String predicateIRI) {
        return getThings(predicateIRI, Thing.class, Thing.Builder.class);
    }

    // TODO Remove after creating and replacing all with #getThings(HasPredicateIRI, Class, Class)
    default <T extends Thing> Iterable<T> getThings(HasPredicateIRI predicate, Class<T> klass) {
        return getThings(predicate.iri(), klass);
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

    default boolean hasLink(String predicateIRI, String linkIRI) {
        var links = getLinks(predicateIRI);
        for (var link : links) if (link.toString().equals(linkIRI)) return true;
        return false;
    }

    // TODO Collection<> get N for other things than Links

    // TODO get... other types.

    Builder<? extends PredicatesObjects> copy();

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    interface Builder<B extends PredicatesObjects> // skipcq: JAVA-E0169
            extends dev.enola.common.Builder<B> {

        // TODO Add clear(String predicateIRI) method..

        @CanIgnoreReturnValue
        @SuppressWarnings("Immutable")
        default Builder<B> set(String predicateIRI, Link link) {
            set(predicateIRI, (Object) link); // !
            return this;
        }

        @CanIgnoreReturnValue
        default Builder<B> set(String predicateIRI, HasIRI hasIRI) {
            set(predicateIRI, new Link(hasIRI.iri())); // !
            return this;
        }

        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> set(String predicateIRI, T value);

        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> set(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        /**
         * Adds one of possibly several value objects for the given predicate IRI.
         *
         * <p>This is UNORDERED! Insertion order may NOT be preserved. Duplicates are not allowed
         * and will cause an error (possibly only on {@link #build()}). It is an error if this
         * property has already been set to anything else than a {@link Set}.
         */
        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> add(String predicateIRI, T value);

        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> addAll(String predicateIRI, Iterable<T> values);

        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> add(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> addAll(
                String predicateIRI, Iterable<T> values, @Nullable String datatypeIRI);

        /**
         * Adds one of possibly several value objects for the given predicate IRI - and preserves
         * order.
         *
         * <p>Nota bene: This is <b>ordered</b> (insertion order <i>is</i> preserved), but not
         * <b>sorted</b> (elements are in the order in which they were added, not [re-]sorted).
         *
         * <p>If this property has previously already been set to something other than a List, then
         * it gets converted to a list (in a random order), and then values gets added.
         *
         * <p>Duplicates ARE allowed.
         */
        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> addOrdered(String predicateIRI, T value);

        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> addOrdered(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        @CanIgnoreReturnValue
        <@ImmutableTypeParameter T> Builder<B> addAllOrdered(
                String predicateIRI, Iterable<T> values);

        @CanIgnoreReturnValue
        default <@ImmutableTypeParameter T> Builder<B> add(HasPredicateIRI predicate, T value) {
            return add(predicate.iri(), value);
        }

        @CanIgnoreReturnValue
        default <@ImmutableTypeParameter T> Builder<B> addAll(
                HasPredicateIRI predicate, Iterable<T> value) {
            return addAll(predicate.iri(), value);
        }

        @CanIgnoreReturnValue
        default <@ImmutableTypeParameter T> Builder<B> add(
                HasPredicateIRI predicate, T value, @Nullable String datatypeIRI) {
            return add(predicate.iri(), value, datatypeIRI);
        }

        @CanIgnoreReturnValue
        default <@ImmutableTypeParameter T> Builder<B> addAll(
                HasPredicateIRI predicate, Iterable<T> value, @Nullable String datatypeIRI) {
            return addAll(predicate.iri(), value, datatypeIRI);
        }

        @CanIgnoreReturnValue
        default <@ImmutableTypeParameter T> Builder<B> addOrdered(
                HasPredicateIRI predicate, T value) {
            return addOrdered(predicate.iri(), value);
        }

        @CanIgnoreReturnValue
        default <@ImmutableTypeParameter T> Builder<B> addOrdered(
                HasPredicateIRI predicate, T value, @Nullable String datatypeIRI) {
            return addOrdered(predicate.iri(), value, datatypeIRI);
        }

        @Override
        B build();
    }
}
