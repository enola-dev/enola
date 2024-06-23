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
package dev.enola.thing.resource;

import dev.enola.common.io.resource.Resource;
import dev.enola.thing.Thing;

import java.util.stream.Stream;

interface ResourceToThingsConverter { // TODO Converter<Resource, Stream<Thing>>
    // TODO Retrofit the thing the CLI does to --load RDF into this

    Stream<Thing> convert(Resource resource);
}
