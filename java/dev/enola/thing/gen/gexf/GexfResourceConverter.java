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
package dev.enola.thing.gen.gexf;

import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.thing.io.Loader;

public class GexfResourceConverter implements CatchingResourceConverter {

    private final GexfGenerator gexfGenerator;
    private final Loader loader;

    public GexfResourceConverter(Loader loader, GexfGenerator gexfGenerator) {
        this.gexfGenerator = gexfGenerator;
        this.loader = loader;
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {
        if (!MediaTypes.normalizedNoParamsEquals(into.mediaType(), GexfMediaType.GEXF))
            return false;

        var things = loader.loadAtLeastOneThing(from.uri());
        gexfGenerator.convertIntoOrThrow(things, into);
        return true;
    }
}
