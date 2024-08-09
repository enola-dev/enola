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

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.data.Store;
import dev.enola.thing.Thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.stream.Stream;

public class Loader implements ConverterInto<Stream<URI>, Store<?, Thing>> {

    // TODO Load resources multi-threaded, in parallel...

    private static final Logger LOG = LoggerFactory.getLogger(Loader.class);

    private final ResourceProvider rp;
    private final ResourceIntoThingConverters resourceIntoThingConverters;

    public Loader(ResourceProvider rp, ResourceIntoThingConverters resourceIntoThingConverters) {
        this.resourceIntoThingConverters = resourceIntoThingConverters;
        this.rp = rp;
    }

    @Override
    public boolean convertInto(Stream<URI> stream, Store<?, Thing> store)
            throws ConversionException, IOException {

        stream.forEach(resource -> load(resource, store));
        return true;
    }

    private void load(URI uri, Store<?, Thing> store) {
        LOG.info("Loading {}...", uri);
        try {
            var resource = rp.getResource(uri);
            var things = resourceIntoThingConverters.convert(resource);
            things.forEach(
                    thingBuilder -> {
                        var thing = thingBuilder.build();
                        store.merge(thing);
                    });

        } catch (Exception e) {
            LOG.error("Failed to load: {}", uri, e);
            // Don't rethrow, let loading other resources continue
        }
    }
}
