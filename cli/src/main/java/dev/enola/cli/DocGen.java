/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.core.docgen.MarkdownDocGenerator;
import dev.enola.core.docgen.Options;
import dev.enola.core.meta.EntityKindRepository;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.net.URI;

@Command(name = "docgen", description = "Generate Markdown Documentation")
public class DocGen implements CheckedRunnable {

    @CommandLine.Option(
            names = {"--model"},
            required = true,
            description = "URI to EntityKinds (e.g. file:model.yaml)")
    URI model;

    @CommandLine.Option(
            names = {"--output", "-o"},
            required = true,
            defaultValue = "fd:1?charset=UTF-8", // = FileDescriptorResource.OUT
            description = "URI of where to write generated documentation")
    URI output;

    @CommandLine.Option(
            names = {"--diagram", "-d"},
            required = true,
            defaultValue = "Mermaid",
            description = "Type of diagrams to generate")
    Options.DiagramType diagram;

    @Override
    public void run() throws Exception {
        var modelResource = new ResourceProviders().getReadableResource(model);

        var ekr = new EntityKindRepository();
        ekr.load(modelResource);

        var options = new Options();
        options.diagram = diagram;

        var resource = new ResourceProviders().getWritableResource(output);
        try (var writer = resource.charSink().openBufferedStream()) {
            new MarkdownDocGenerator(options).render(ekr, writer);
        }
    }
}
