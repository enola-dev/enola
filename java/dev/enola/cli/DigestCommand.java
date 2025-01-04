/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
import dev.enola.common.io.hashbrown.Multihashes;
import dev.enola.common.io.hashbrown.ResourceHasher;
import dev.enola.common.io.iri.URIs;

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

import picocli.CommandLine;

import java.nio.file.Paths;

@CommandLine.Command(
        name = "digest",
        description =
                "Generates a Message Authentication Code (MAC; also Message Integrity Code, MIC)"
                        + " for a given URL.\n"
                        + "See also the related ?integrity=... argument of the 'fetch' command.")
public class DigestCommand extends CommandWithResourceProvider {

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(index = "0", paramLabel = "url", description = "URL")
    String url;

    @Override
    public Integer call() throws Exception {
        super.run();
        var pw = spec.commandLine().getOut();

        var uri = URIs.parse(url);
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            var resource = rp.getResource(uri);

            var multihash = new ResourceHasher().hash(resource, Multihash.Type.sha2_512);
            pw.println(Multihashes.toString(multihash, Multibase.Base.Base64));
        }
        return 0;
    }
}
