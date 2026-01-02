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
package dev.enola.ai.dotprompt;

import static dev.enola.common.template.handlebars.HandlebarsMediaType.HANDLEBARS;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.object.jackson.YamlObjectReaderWriter;
import dev.enola.common.io.resource.MarkdownResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.template.TemplateProvider;
import dev.enola.common.template.handlebars.HandlebarsTemplateProvider;

import java.io.IOException;
import java.net.URI;

public class DotPromptLoader {

    private final ResourceProvider resourceProvider;
    private final TemplateProvider templateProvider;
    private final String defaultModel;

    public DotPromptLoader(ResourceProvider resourceProvider, URI defaultModel) {
        this.resourceProvider = resourceProvider;
        this.templateProvider = new HandlebarsTemplateProvider();
        this.defaultModel = defaultModel.toString();
    }

    public DotPrompt load(URI uri) throws IOException {
        var resource = resourceProvider.getNonNull(uri);
        var md = new MarkdownResource(resource, HANDLEBARS);

        var reader = new YamlObjectReaderWriter();
        var dotPrompt = reader.read(md.frontMatter(), DotPrompt.class);
        dotPrompt.id = uri;

        var templateResource = md.markdown();
        dotPrompt.prompt = templateResource.charSource().read();
        dotPrompt.template = templateProvider.get(templateResource);

        if (dotPrompt.name == null)
            dotPrompt.name = URIs.getFilenameWithoutExtension(uri, ".prompt.md", ".prompt");
        // TODO Handle dotPrompt.variant ...
        if (dotPrompt.model == null) dotPrompt.model = defaultModel;

        // TODO Share code with AgentsLoader!

        // TODO https://github.com/google/dotprompt/issues/307 Translate Picoschema to JSON Schema

        // TODO Validate dotPrompt.input.schema & dotPrompt.input.schema with a JSON Schema
        // Validator

        // TODO Set I&O schemas into (to be added) properties on LoadedDotPrompt (but for what?)

        // TODO Support https://google.github.io/dotprompt/reference/template/#dotprompt-helpers

        return dotPrompt;
    }
}
