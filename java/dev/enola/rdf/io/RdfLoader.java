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
package dev.enola.rdf.io;

import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.io.Loader;
import dev.enola.thing.io.UriIntoThingConverters;

/**
 * RdfLoader is a {@link dev.enola.thing.io.Loader} that's configured to load (only) RDF resources.
 */
public class RdfLoader extends Loader {

    public RdfLoader() {
        super(new UriIntoThingConverters(new RdfResourceIntoThingConverter<>()));
    }

    public RdfLoader(ResourceProvider rp, DatatypeRepository datatypeRepository) {
        super(
                new UriIntoThingConverters(
                        new RdfResourceIntoThingConverter<>(rp, datatypeRepository)));
    }
}
