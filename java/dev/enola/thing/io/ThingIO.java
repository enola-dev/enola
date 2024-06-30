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

import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.thing.Thing;

import java.net.URI;

public class ThingIO {

    // TODO Support a more "streaming" API...

    private final ResourceProvider rp = null; // TODO NOT new ResourceProviders();

    private final ResourceIntoThingConverters<Thing> converter =
            new ResourceIntoThingConverters<>();

    public Iterable<Thing> read(URI uri) {
        return read(uri, Thing.class);
    }

    public <T extends Thing> Iterable<T> read(URI uri, Class<T> klass) {
        var resource = rp.getReadableResource(uri);
        return null; // TODO converter.convert(resource);
    }

    public void write(Iterable<Thing> things, URI uri) {}
}
