/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import io.ipfs.cid.Cid;

import picocli.CommandLine;

@CommandLine.Command(name = "cid", description = "Deconstructs a CID into its components")
public class CidCommand implements Runnable {

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(index = "0", paramLabel = "cid", description = "CID")
    String cidString;

    @Override
    public void run() {
        var cid = Cid.decode(cidString);
        var pw = spec.commandLine().getOut();
        pw.print("CID v" + cid.version + " - " + cid.codec.name());
        if (cid.version > 0)
            pw.print(" - " + MultihashCommand.toString(cidString, cid.bareMultihash()));
        pw.println();
    }
}
