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

import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;

import java.io.IOException;
import java.nio.charset.Charset;

class ThrowingTemplateLoader implements TemplateLoader {

    // Because HandlebarsTemplateProvider only ever uses Handlebars.compile(TemplateSource),
    // none of these methods should ever be called. We just prefer passing this to new Handlebars()
    // out of an abundance of caution to prevent resources on the classpath from getting
    // inadvertently exposed (because the Handlebars default constructor uses the
    // ClassPathTemplateLoader.

    @Override
    public TemplateSource sourceAt(String location) throws IOException {
        throw new IOException("Intentionally not implemented, should never be called?!");
    }

    @Override
    public String resolve(String location) {
        throw new IllegalStateException("Intentionally not implemented, should never be called?!");
    }

    @Override
    public String getPrefix() {
        throw new IllegalStateException("Intentionally not implemented, should never be called?!");
    }

    @Override
    public String getSuffix() {
        throw new IllegalStateException("Intentionally not implemented, should never be called?!");
    }

    @Override
    public void setPrefix(String prefix) {
        throw new IllegalStateException("Intentionally not implemented, should never be called?!");
    }

    @Override
    public void setSuffix(String suffix) {
        throw new IllegalStateException("Intentionally not implemented, should never be called?!");
    }

    @Override
    public void setCharset(Charset charset) {
        throw new IllegalStateException("Intentionally not implemented, should never be called?!");
    }

    @Override
    public Charset getCharset() {
        throw new IllegalStateException("Intentionally not implemented, should never be called?!");
    }
}
