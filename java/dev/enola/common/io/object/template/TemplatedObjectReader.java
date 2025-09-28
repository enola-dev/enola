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
package dev.enola.common.io.object.template;

import com.google.common.net.MediaType;

import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.StringResource2;
import dev.enola.common.template.TemplateProvider;
import dev.enola.common.template.handlebars.HandlebarsMediaType;
import dev.enola.common.template.handlebars.HandlebarsTemplateProvider;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class TemplatedObjectReader implements ObjectReader {

    private final ObjectReader delegate;
    private final TemplateProvider templateProvider = new HandlebarsTemplateProvider();
    private final MediaType templatesMediaType = HandlebarsMediaType.HANDLEBARS;

    public TemplatedObjectReader(ObjectReader delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException {
        var uri = resource.uri();
        var mediaType = resource.mediaType();
        var rawTemplate = resource.charSource().read();

        var rawTemplateResource = StringResource2.of(rawTemplate, mediaType, uri);
        var map = delegate.optional(rawTemplateResource, Map.class);
        if (map.isEmpty()) return Optional.empty();

        var templateResource = StringResource2.of(rawTemplate, templatesMediaType, uri);
        var templatedText = templateProvider.get(templateResource).apply(map.get());
        var templatedResource = StringResource2.of(templatedText, mediaType, uri);
        return delegate.optional(templatedResource, type);
    }

    @Override
    public <T> Iterable<T> readArray(ReadableResource resource, Class<T> type) throws IOException {
        throw new UnsupportedOperationException("TODO implemented method 'readArray'");
    }

    @Override
    public <T> Iterable<T> readStream(ReadableResource resource, Class<T> type) throws IOException {
        throw new UnsupportedOperationException("TODO implemented method 'readStream'");
    }
}
