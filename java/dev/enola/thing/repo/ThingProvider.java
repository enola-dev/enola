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
package dev.enola.thing.repo;

import com.google.common.collect.Iterables;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.data.ProviderFromIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.ThingConverterInto;
import dev.enola.thing.impl.OnlyIRIThing;
import dev.enola.thing.java.TBF;
import dev.enola.thing.message.ProtoThingProvider;

import org.jspecify.annotations.Nullable;

import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides {@link Thing}s.
 *
 * <p>Intentionally named a *Provider and not a *Repository, because implementations may or may not
 * "fetch from" a "data store" (which *Repository often implies); as some might indeed, but others
 * may well not, and just "conjure up" new Things out of thin air, based solely on the IRI!
 *
 * <p>See {@link ProtoThingProvider} for a Proto Thing variant (this is for the Java Thing).
 *
 * <p>See {@link AlwaysThingProvider} for a {@link org.jspecify.annotations.NonNull} variant.
 */
@FunctionalInterface
public interface ThingProvider extends ProviderFromIRI<Thing> {

    /**
     * ThingProvider which is looked up in the {@link TLC}.
     *
     * <p>Consider possibly using {@link AlwaysThingProvider#CTX} instead.
     */
    // TODO Eventually migrate everything to AlwaysThingProvider...
    ThingProvider CTX = iri -> TLC.get(ThingProvider.class).get(iri);

    default Thing.Builder<?> getBuilder(String iri, String typeIRI) {
        var thing = get(Objects.requireNonNull(iri, "iri"));
        if (thing != null && !(thing instanceof OnlyIRIThing)) return thing.copy();
        else return TLC.get(TBF.class).create(typeIRI).iri(iri);
    }

    default Thing.Builder<?> getBuilder(String iri) {
        var thing = get(Objects.requireNonNull(iri, "iri"));
        if (thing != null) return thing.copy();
        else return TLC.get(TBF.class).create().iri(iri);
    }

    /**
     * Get the Thing.
     *
     * @param iri an IRI
     * @return a Thing [TODO: never null; but may be an empty Thing for an unknown IRI]
     * @throws UncheckedIOException if there was something at that IRI, but it could not be read
     * @throws ConversionException if there was a problem converting what was at the IRI to a Thing
     */
    // TODO Remove ConversionException? It's weird, and should never happen anymore (here)...
    @Override
    // TODO Eventually remove @Nullable by making all ThingProvider be AlwaysThingProvider
    @Nullable Thing get(String iri) throws UncheckedIOException, ConversionException;

    default Optional<Thing> getOptional(String iri)
            throws UncheckedIOException, ConversionException {
        return Optional.ofNullable(get(iri));
    }

    // TODO Switch (back?!) from UncheckedIOException to IOException (as documented)

    // TODO Move get with Class<T> up into ProviderFromIRI?

    @SuppressWarnings("unchecked")
    @Deprecated // TODO Replace all callers with new variant, see below
    default <T extends Thing> @Nullable T get(String iri, Class<T> thingClass)
            throws UncheckedIOException, ConversionException {
        Thing thing = get(Objects.requireNonNull(iri, "iri"));
        // Nota bene: This happens when a Thing doesn't not have a @rdf:type
        // TODO What would be a better way to handle this more gracefully?
        //   return null is a bad idea - because it's there, just not of type
        //   generate the Proxy of the Thing here, and return that?!
        //   But that could hide bugs, and be inefficient, confusing...
        if (thing != null && !thingClass.isInstance(thing))
            throw new IllegalArgumentException(
                    iri + " is " + thing.getClass() + ", not " + thingClass);
        return (T) thing;
    }

    @SuppressWarnings("unchecked")
    default <T extends Thing, B extends Thing.Builder<T>> B getBuilder(
            String iri, Class<T> thingClass, Class<B> thingBuilderClass) {
        Thing thing = get(Objects.requireNonNull(iri, "iri"));

        if (thing != null && thingClass.isInstance(thing)) {
            // noinspection unchecked
            return (B) thing.copy();
        }

        // TODO Avoid creating both of these kinda unnecessary intermediate Builders...
        var tbf = TLC.get(TBF.class);
        if (thing == null) return (B) tbf.create(thingBuilderClass, thingClass).iri(iri);

        var builder = tbf.create(thingBuilderClass, thingClass, thing.predicateIRIs().size());
        new ThingConverterInto().convertInto(thing, builder);
        return builder;
    }

    default <T extends Thing, B extends Thing.Builder<T>> T get(
            String iri, Class<T> thingClass, Class<B> thingBuilderClass) {
        Thing thing = get(Objects.requireNonNull(iri, "iri"));

        if (thing != null && thingClass.isInstance(thing)) {
            // noinspection unchecked
            return (T) thing;
        }

        // TODO Avoid creating both of these kinda unnecessary intermediate Builders...
        var tbf = TLC.get(TBF.class);
        if (thing == null) return tbf.create(thingBuilderClass, thingClass).iri(iri).build();

        var builder = tbf.create(thingBuilderClass, thingClass, thing.predicateIRIs().size());
        new ThingConverterInto().convertInto(thing, builder);
        return builder.build();
    }

    default <T extends Thing, B extends Thing.Builder<T>> Iterable<T> get(
            Iterable<Object> iris, Class<T> thingClass, Class<B> thingBuilderClass) {
        return Iterables.transform(iris, iri -> get(iri.toString(), thingClass, thingBuilderClass));
    }

    @Deprecated // TODO Replace with new variant with Class<B> thingBuilderClass, see above
    default <T extends Thing> Iterable<T> get(Iterable<String> iris, Class<T> thingClass) {
        return Iterables.transform(iris, iri -> get(iri, thingClass));
    }

    // NOT Iterable/Stream<Thing> not just 1x Thing, just like in ProtoThingProvider
}
