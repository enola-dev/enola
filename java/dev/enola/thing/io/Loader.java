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
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.data.Store;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Stream;

public class Loader implements ConverterInto<Stream<ReadableResource>, Store<?, Thing>> {

    // TODO Do this multi-threaded, in parallel...

    private static final Logger LOG = LoggerFactory.getLogger(Loader.class);

    private final ResourceIntoThingConverter resourceIntoThingConverter;

    public Loader(ResourceIntoThingConverter resourceIntoThingConverter) {
        this.resourceIntoThingConverter = resourceIntoThingConverter;
    }

    @Override
    public boolean convertInto(Stream<ReadableResource> stream, Store<?, Thing> store)
            throws ConversionException, IOException {

        stream.forEach(resource -> load(resource, store));
        return true;
    }

    private void load(ReadableResource resource, Store<?, Thing> store) {
        LOG.info("Loading {}...", resource);
        var things = resourceIntoThingConverter.convert(resource);
        if (!things.isPresent()) LOG.error("No Things in loaded: {}", resource);
        else {
            things.get()
                    .forEach(
                            thingBuilder -> {
                                thingBuilder.set(KIRI.E.ORIGIN, resource.uri());
                                var thing = thingBuilder.build();
                                store.merge(thing);
                            });
        }
    }
}
