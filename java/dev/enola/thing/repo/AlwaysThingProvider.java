/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.OnlyIRIThing;

import org.jspecify.annotations.NonNull;

import java.io.UncheckedIOException;
import java.util.Optional;

/**
 * AlwaysThingProvider is a {@link ThingProvider} which never returns null, but always a Thing.
 *
 * <p>The Thing WILL have the requested IRI - but if no such Thing is known (e.g. was pre-loaded, or
 * could be fetched), then the returned Thing ONLY has an IRI - and no properties (nor datatypes).
 *
 * <p>This is useful in some situations where callers don't want to distinguish "non-existent"
 * Things; e.g. to have a {@link dev.enola.thing.metadata.ThingMetadataProvider} always work, or so.
 * Callers which do need to distinguish can still use {@link #getOpt(String)}.
 */
public class AlwaysThingProvider implements ThingProvider {

    public static final AlwaysThingProvider CTX =
            new AlwaysThingProvider(
                    // TODO Rethink EmptyThingProvider... convenient - but hides errors! So not?
                    TLC.optional(ThingProvider.class).orElse(EmptyThingProvider.INSTANCE));

    ThingProvider delegate;

    public AlwaysThingProvider(ThingProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NonNull Thing get(String iri) throws UncheckedIOException, ConversionException {
        var thing = delegate.get(iri);
        if (thing == null) return new OnlyIRIThing(iri);
        else return thing;
    }

    /*
    @Override
    public <T extends Thing> @NonNull T get(String iri, Class<T> thingClass)
            throws UncheckedIOException, ConversionException {
        // TODO Use a TBF, like ProxyTBF, to create an instance of thingClass...
        return ThingProvider.super.get(iri, thingClass);
    }
    */

    @Override
    public Optional<Thing> getOptional(String iri)
            throws UncheckedIOException, ConversionException {
        var thing = delegate.get(iri);
        return Optional.ofNullable(thing);
    }
}
