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

import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs;

import picocli.CommandLine;

import java.nio.file.Paths;

@CommandLine.Command(
        name = "change",
        description =
                "Given a URL, obtains its 'Change Token'; or given one, verifies if content at that"
                        + " URL has changed.\n")
public class ChangeCommand extends CommandWithResourceProvider {

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(index = "0", paramLabel = "url", description = "URL")
    String url;

    @CommandLine.Parameters(
            index = "1",
            paramLabel = "previous",
            description = "Previously obtained ChangeToken for this URL",
            arity = "0..1")
    String previous;

    @Override
    public Integer call() throws Exception {
        super.run();

        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            var uri = URIs.parse(url);
            var resource = rp.getResource(uri);

            var pw = spec.commandLine().getOut();
            if (previous == null) {
                pw.println(resource.changeToken());
            } else {
                pw.println(resource.isDifferent(previous));
            }
        }

        return 0;
    }
}
