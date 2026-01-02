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

import dev.enola.common.io.mediatype.MediaTypeProviders;

import picocli.CommandLine;

@CommandLine.Command(
        name = "mediatypes",
        description = "Provides information about all supported media types for Thing Resource I/O")
public class MediaTypeInfoCommand implements Runnable {

    // TODO Replace this with enola:MediaType's enola:fileExtensions from mediaTypes.ttl

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        var pw = spec.commandLine().getOut();

        for (var entry :
                MediaTypeProviders.SINGLETON.get().knownTypesWithAlternatives().entrySet()) {
            var alt = entry.getValue();
            pw.println("." + entry.getKey() + (!alt.isEmpty() ? " == " + alt : ""));
        }
    }
}
