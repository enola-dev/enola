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
package dev.enola.common.template.handlebars;

import static dev.enola.common.template.handlebars.HandlebarsMediaType.HANDLEBARS;

import com.github.jknack.handlebars.Handlebars;

import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.ResourceProvider;
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

    private final ResourceProvider rp;

    private final Handlebars handlebars = new Handlebars(new ThrowingTemplateLoader());

    public HandlebarsTemplateProvider(ResourceProvider rp) {
        this.rp = rp;
    }

    @Override
    public Optional<Template> optional(URI origin) throws IOException {
        var resource = rp.getResource(origin);
        if (!MediaTypes.normalizedNoParamsEquals(resource.mediaType(), HANDLEBARS))
            return Optional.empty();

        var resourceTemplateSource = new ResourceTemplateSource(resource);
        var template = handlebars.compile(resourceTemplateSource);
        return Optional.of(new HandlebarTemplate(origin, template));
    }

    private static class HandlebarTemplate implements Template {
        private final com.github.jknack.handlebars.Template handlebarsTemplate;
        private final URI origin;

        HandlebarTemplate(URI origin, com.github.jknack.handlebars.Template template) {
            this.handlebarsTemplate = template;
            this.origin = origin;
        }

        @Override
        public URI origin() {
            return origin;
        }

        @Override
        public void apply(Object input, Appendable output) throws IOException {
            handlebarsTemplate.apply(input, new AppendableWriter(output));
        }
    }
}
