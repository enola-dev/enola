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
package dev.enola.common.template.handlebars;

import static com.google.common.net.MediaType.JSON_UTF_8;

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.template.handlebars.HandlebarsMediaType.HANDLEBARS;

import com.github.jknack.handlebars.Handlebars;

import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.util.AppendableWriter;
import dev.enola.common.template.Template;
import dev.enola.common.template.TemplateProvider;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * {@link TemplateProvider} implementation (currently) based on jknack's <a
 * href="https://jknack.github.io/handlebars.java/">Handlebars.java</a>.
 */
public class HandlebarsTemplateProvider implements TemplateProvider {

    private final Handlebars handlebars;

    public HandlebarsTemplateProvider() {
        handlebars = new Handlebars(new ThrowingTemplateLoader());
        handlebars.registerHelper("gav", new GavHelper());
    }

    @Override
    public Optional<Template> optional(ReadableResource resource) throws IOException {
        // NB: We accept *.handlebars as well as *.yaml and *.json, to support them as templates
        if (!MediaTypes.normalizedNoParamsEquals(
                resource.mediaType(), HANDLEBARS, YAML_UTF_8, JSON_UTF_8)) return Optional.empty();

        var resourceTemplateSource = new ResourceTemplateSource(resource);
        var template = handlebars.compile(resourceTemplateSource);
        return Optional.of(new HandlebarTemplate(resource, template));
    }

    private static class HandlebarTemplate implements Template {
        private final com.github.jknack.handlebars.Template handlebarsTemplate;
        private final URI origin;

        HandlebarTemplate(
                ReadableResource resource, com.github.jknack.handlebars.Template template) {
            this.handlebarsTemplate = template;
            this.origin = resource.uri();
        }

        @Override
        public URI origin() {
            return origin;
        }

        @Override
        public void apply(Object input, Appendable output) throws IOException {
            try (var writer = new AppendableWriter(output)) {
                handlebarsTemplate.apply(input, writer);
                writer.flush();
            }
        }
    }
}
