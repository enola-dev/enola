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
package dev.enola.thing.gen.graphviz;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.thing.io.Loader;
import dev.enola.thing.io.UriIntoThingConverters;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;

import java.util.stream.Stream;

public class GraphvizResourceConverter implements CatchingResourceConverter {

    private final GraphvizGenerator graphvizGenerator;

    public GraphvizResourceConverter(GraphvizGenerator graphvizGenerator) {
        this.graphvizGenerator = graphvizGenerator;
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {
        if (!MediaTypes.normalizedNoParamsEquals(into.mediaType(), GraphvizMediaType.GV))
            return false;

        var uriIntoThingConverters = new UriIntoThingConverters();
        var loader = new Loader(uriIntoThingConverters);
        var store = new ThingMemoryRepositoryROBuilder();
        if (!loader.convertInto(Stream.of(from.uri()), store)) {
            throw new ConversionException("Nothing loaded from: " + from);
        }

        var things = store.build().list();
        try (var out = into.charSink().openBufferedStream()) {
            graphvizGenerator.convertIntoOrThrow(things, out);
            return true;
        }
    }
}
