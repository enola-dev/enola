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
package dev.enola.cli;

import dev.enola.common.context.Context;
import dev.enola.format.tika.TikaThingConverter;
import dev.enola.format.xml.XmlThingConverter;
import dev.enola.model.enola.files.FileThingConverter;
import dev.enola.model.enola.mediatype.TikaMediaTypesThingConverter;
import dev.enola.rdf.io.RdfResourceIntoThingConverter;
import dev.enola.thing.io.Loader;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.io.UriIntoThingConverters;

import picocli.CommandLine;

import java.util.ArrayList;

public abstract class CommandWithResourceProviderAndLoader extends CommandWithResourceProvider {

    @CommandLine.Option(
            names = {"--file-loader"},
            negatable = true,
            required = true,
            defaultValue = "true",
            fallbackValue = "true",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Whether file:/ resources create File (or Directory) Things")
    boolean fileLoader;

    @CommandLine.Option(
            names = {"--tika-loader"},
            negatable = true,
            required = true,
            defaultValue = "true",
            fallbackValue = "true",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Whether resources are loaded with Tika parsers to create Things")
    boolean tikaLoader;

    protected Loader loader() {
        // TODO Move this (and other) initialization out of CLI, to a dev.enola.Enola...
        var uriIntoThingConverters = new ArrayList<UriIntoThingConverter>(7);
        uriIntoThingConverters.add(new RdfResourceIntoThingConverter<>());
        uriIntoThingConverters.add(new XmlThingConverter(rp));
        if (fileLoader) uriIntoThingConverters.add(new FileThingConverter());
        if (tikaLoader) uriIntoThingConverters.add(new TikaThingConverter(rp));
        uriIntoThingConverters.add(new TikaMediaTypesThingConverter());

        var ritc = new UriIntoThingConverters(uriIntoThingConverters);
        return new Loader(ritc);
    }

    @Override
    protected void setup(Context ctx) {
        super.setup(ctx);

        // TODO Rethink this in a better way...
        // This was originally motivated by keeping e.g. the mediaType/graph.gv.svg clean;
        // because it looks overwhelming and ugly with the links to mediaTypes.ttl, from everything.
        // Perhaps later when we can properly filter what to render (probably with sparql:...), then
        // there will be a better way for this, and no need for this slight hack?
        //
        // Only set enola:origin if --file-loader was enabled
        ctx.push(UriIntoThingConverters.Flags.ORIGIN, fileLoader);
    }
}
