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
package dev.enola.thing.gen.markdown;

import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.thing.Thing;
import dev.enola.thing.gen.Relativizer;

import java.io.IOException;
import java.net.URI;

public class MarkdownSiteGenerator {

    private final URI base;
    private final ResourceProvider rp;
    private final MarkdownThingGenerator mtg;

    public MarkdownSiteGenerator(URI base, ResourceProvider rp, MarkdownThingGenerator mtg) {
        this.base = base;
        this.mtg = mtg;
        this.rp = rp;
    }

    public void generate(Iterable<Thing> things) throws IOException {
        // TODO Do this multi-threaded, in parallel...
        for (var thing : things) {
            var thingIRI = thing.iri();
            var relativeThingIRI = Relativizer.relativize(URI.create(thingIRI), "md");
            var outputIRI = base.resolve(relativeThingIRI);
            var outputResource = rp.getWritableResource(outputIRI);
            try (var writer = outputResource.charSink().openBufferedStream()) {
                mtg.generate(thing, writer, outputIRI, base);
            }
        }
    }
}
