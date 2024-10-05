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

import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.MediaTypeProviders;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Paths;

@Command(
        name = "detect",
        description =
                "Provides information about the media type detected for a given URL.\n"
                    + "This works both for local files (based on extension), and remote HTTP (based"
                    + " on headers).")
public class DetectCommand extends CommandWithResourceProvider {

    @Spec CommandSpec spec;

    @CommandLine.Parameters(index = "0", paramLabel = "iri", description = "IRI")
    String iri;

    @Override
    public void run() throws Exception {
        super.run();
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            var resource = rp.getResource(URIs.parse(iri));
            var mediaType =
                    MediaTypeProviders.SINGLETON
                            .get()
                            .detect(resource)
                            .orElse(resource.mediaType());

            var pw = spec.commandLine().getOut();
            pw.println(mediaType);
        }
    }
}
