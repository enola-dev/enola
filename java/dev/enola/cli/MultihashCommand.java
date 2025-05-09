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

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

import picocli.CommandLine;

@CommandLine.Command(
        name = "multihash",
        description = "Deconstructs a Multihash into its components")
public class MultihashCommand implements Runnable {

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(index = "0", paramLabel = "multihash", description = "Multihash")
    String multihashString;

    @Override
    public void run() {
        var multihash = Multihash.decode(multihashString);
        var pw = spec.commandLine().getOut();
        pw.println(toString(multihashString, multihash));
    }

    static String toString(String multihashString, Multihash multihash) {
        var base = Multibase.encoding(multihashString);
        return base.name()
                + " - "
                + multihash.getType()
                + " - "
                + multihash.getHash().length
                + " bytes";
    }
}
