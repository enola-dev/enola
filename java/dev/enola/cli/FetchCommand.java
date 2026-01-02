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

import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs;

import picocli.CommandLine;

import java.net.URI;
import java.nio.file.Paths;

@CommandLine.Command(
        name = "fetch",
        description = {"Fetches (I/O) a Resource", "See also the related 'info detect' command."})
public class FetchCommand extends CommandWithResourceProvider {

    @CommandLine.Parameters(index = "0", paramLabel = "URL", description = "URL to fetch")
    String url;

    @Override
    public Integer call() throws Exception {
        super.run();
        var uri = URI.create(url);
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            var resource = rp.getResource(uri);
            if (resource == null) {
                System.err.println(uri.getScheme() + " scheme unknown; try: enola fetch --help");
                return 1;

            } else {
                resource.byteSource().copyTo(System.out);
                return 0;
            }
        }
    }
}
