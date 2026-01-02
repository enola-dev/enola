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
package dev.enola.thing.gen.graphcommons;

import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.thing.io.Loader;

public class GraphCommonsResourceConverter implements CatchingResourceConverter {

    private final GraphCommonsJsonGenerator graphCommonsGenerator;
    private final Loader loader;

    public GraphCommonsResourceConverter(
            Loader loader, GraphCommonsJsonGenerator graphCommonsGenerator) {
        this.loader = loader;
        this.graphCommonsGenerator = graphCommonsGenerator;
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {
        if (!MediaTypes.normalizedNoParamsEquals(into.mediaType(), GraphCommonsMediaType.GCJSON))
            return false;

        var things = loader.loadAtLeastOneThing(from.uri());
        graphCommonsGenerator.convertIntoOrThrow(things, into);
        return true;
    }
}
