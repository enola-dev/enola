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

import dev.enola.common.convert.ConversionException;
import dev.enola.data.ProviderFromIRI;
import dev.enola.thing.proto.Thing;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Provides things.
 *
 * <p>Intentionally named a *Provider and not a *Repository, because implementations may or may not
 * "fetch from" a "data store" (which *Repository often implies); as some might indeed, but others
 * may well not, and just "conjure up" new Things out of thin air, based solely on the IRI!
 */
public interface ThingProvider extends ProviderFromIRI<Thing> {
    // TODO Move this into the .message sub-package, as that's what this is about
    // TODO Later consider merging this with ThingService?

    /**
     * Get the Thing.
     *
     * @param iri an IRI
     * @return a Thing, never null; but may be an empty Thing for an unknown IRI
     * @throws IOException if there was something at that IRI but it could not be read
     * @throws ConversionException if there was a problem converting what was at the IRI to a Thing
     */
    @Override
    // TODO Things not just 1x Thing?
    Thing get(String iri) throws UncheckedIOException, ConversionException;

    // TODO Thing getThings(String iri, int depth) throws IOException;
}
