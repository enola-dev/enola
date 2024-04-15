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

import dev.enola.common.io.mediatype.MediaTypeProviders;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(
        name = "extensions",
        description =
                "Provides information about extensions of media types for URI path resource names")
public class ExtensionsInfoCommand implements CheckedRunnable {

    @Spec CommandSpec spec;

    @Override
    public void run() throws Exception {
        var pw = spec.commandLine().getOut();

        for (var entry : MediaTypeProviders.SINGLETON.extensionsToTypes().entrySet()) {
            pw.println("." + entry.getKey() + ": " + entry.getValue());
        }
    }
}
