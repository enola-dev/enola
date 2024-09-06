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
package dev.enola.thing.io;

import com.google.common.collect.Iterables;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.data.Store;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.stream.Stream;

public class Loader implements ConverterInto<Stream<URI>, Store<?, Thing>> {

    // TODO Move Glob-based loading from CommandWithModel into here!

    // TODO Load resources multi-threaded, in parallel...

    private static final Logger LOG = LoggerFactory.getLogger(Loader.class);

    private final UriIntoThingConverters uriIntoThingConverters;

    public Loader(UriIntoThingConverters uriIntoThingConverters) {
        this.uriIntoThingConverters = uriIntoThingConverters;
    }

    @Override
    public boolean convertInto(Stream<URI> stream, Store<?, Thing> store)
            throws ConversionException, IOException {

        stream.forEach(resource -> load(resource, store));
        // TODO Should check if at least one URI successfully loaded anything?
        return true;
    }

    public boolean load(URI uri, Store<?, Thing> store) {
        LOG.info("Loading {}...", uri);
        var things = uriIntoThingConverters.convert(uri);
        if (Iterables.isEmpty(things)) return false;
        things.forEach(
                thingBuilder -> {
                    var thing = thingBuilder.build();
                    store.merge(thing);
                });
        return true;
    }

    public Iterable<Thing> loadThings(URI uri) {
        var store = new ThingMemoryRepositoryROBuilder();
        load(uri, store);
        return store.build().list();
    }

    public Iterable<Thing> loadAtLeastOneThing(URI uri) {
        var things = loadThings(uri);
        if (Iterables.isEmpty(things)) throw new ConversionException("Nothing loaded from: " + uri);
        return things;
    }
}
