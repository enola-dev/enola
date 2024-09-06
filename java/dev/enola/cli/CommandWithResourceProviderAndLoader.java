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
package dev.enola.cli;

import dev.enola.model.enola.files.FileThingConverter;
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

    protected Loader loader() {
        var uriIntoThingConverters = new ArrayList<UriIntoThingConverter>(2);
        uriIntoThingConverters.add(new RdfResourceIntoThingConverter<>());
        if (fileLoader) uriIntoThingConverters.add(new FileThingConverter());
        var ritc = new UriIntoThingConverters(uriIntoThingConverters);
        return new Loader(ritc);
    }
}
