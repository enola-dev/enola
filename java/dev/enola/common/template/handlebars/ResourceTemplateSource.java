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

import com.github.jknack.handlebars.io.TemplateSource;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.ReadableResource;

import java.io.IOException;
import java.nio.charset.Charset;

final class ResourceTemplateSource implements TemplateSource {

    private final ReadableResource resource;

    ResourceTemplateSource(ReadableResource resource) {
        this.resource = resource;
    }

    @Override
    public String content(Charset charset) throws IOException {
        resource.mediaType()
                .charset()
                .toJavaUtil()
                .ifPresent(
                        cs -> {
                            if (!cs.equals(charset))
                                throw new IllegalArgumentException(
                                        "Unsupported charset: " + charset);
                        });
        return resource.charSource().read();
    }

    @Override
    public String filename() {
        var filename = URIs.getFilename(resource.uri());
        if (filename.isEmpty())
            throw new IllegalArgumentException("No filename: " + resource.uri());
        return filename;
    }

    @Override
    public long lastModified() {
        // ReadableResource doesn't expose an lastModified(), but Handlebars' caching and/or
        // template reloading mechanisms likely anyway just use for a before/after comparison for
        // change detection, so we can delegate to the ChangeToken based hashCode(), which should do
        // what we really need this for. TODO Actually verify this, with real test coverage!
        return hashCode();
    }

    @Override
    public int hashCode() {
        return resource.changeToken().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ResourceTemplateSource other = (ResourceTemplateSource) obj;
        return resource.changeToken().equals(other.resource.changeToken());
    }
}
