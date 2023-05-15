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
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.docgen.MarkdownDocGenerator;
import dev.enola.core.meta.docgen.Options;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.URI;

@Command(name = "docgen", description = "Generate Markdown Documentation")
public class DocGen extends CommandWithModel {

    @Option(
            names = {"--output", "-o"},
            required = true,
            defaultValue = "fd:1?charset=UTF-8", // = FileDescriptorResource.OUT
            description = "URI of where to write generated documentation")
    URI output;

    @Option(
            names = {"--diagram", "-d"},
            required = true,
            defaultValue = "Mermaid",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Type of diagrams to generate (${COMPLETION-CANDIDATES})")
    Options.DiagramType diagram;

    @Option(
            names = {"--header", "-h"},
            required = true,
            defaultValue = "string:%23%20Models%0A",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "URI of Markdown header (e.g. file: or string:<URL-encoded> etc.)")
    URI headerURI;

    @Override
    public void run(EntityKindRepository ekr) throws Exception {
        var options = new Options();
        options.diagram = diagram;

        var rp = new ResourceProviders();
        var resource = rp.getWritableResource(output);
        var header = rp.getReadableResource(headerURI).charSource().read();

        try (var writer = resource.charSink().openBufferedStream()) {
            new MarkdownDocGenerator(options).render(ekr, header, writer);
        }
    }
}
