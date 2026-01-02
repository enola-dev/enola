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
package dev.enola.thing.gen.graphviz;

import static dev.enola.common.io.iri.URIs.getQueryMap;

import dev.enola.common.context.TLC;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.thing.io.Loader;

public class GraphvizResourceConverter implements CatchingResourceConverter {

    /** See {@link GraphvizGenerator.Flags#FULL}. */
    public static final String OUT_URI_QUERY_PARAMETER_FULL = "full";

    private final GraphvizGenerator graphvizGenerator;
    private final Loader loader;

    public GraphvizResourceConverter(Loader loader, GraphvizGenerator graphvizGenerator) {
        this.graphvizGenerator = graphvizGenerator;
        this.loader = loader;
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {
        if (!MediaTypes.normalizedNoParamsEquals(into.mediaType(), GraphvizMediaType.GV))
            return false;

        var things = loader.loadAtLeastOneThing(from.uri());

        try (var ctx = TLC.open()) {
            var full =
                    Boolean.parseBoolean(getQueryMap(into.uri()).get(OUT_URI_QUERY_PARAMETER_FULL));
            ctx.push(GraphvizGenerator.Flags.FULL, full);
            graphvizGenerator.convertIntoOrThrow(things, into);
        }
        return true;
    }
}
