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
package dev.enola.common.template;

import java.io.IOException;
import java.net.URI;

/** Template. Instances are obtained from a {@link TemplateProvider}. */
public interface Template {

    /**
     * Returns the URL of the template's origin.
     *
     * <p>This could be a file path, an HTTP URL, a classpath resource, etc.
     */
    URI origin();

    /**
     * Applies the template to the given input data and writes the result to the output.
     *
     * @param input The data to be used by the template.
     * @param output The Appendable to which the rendered template content will be written.
     * @throws IOException If an I/O error occurs during template rendering. Including an error
     *     specific to template processing occurs (e.g. syntax error, data model mismatch, missing
     *     helper).
     */
    void apply(Object input, Appendable output) throws IOException;

    // TODO JavaDoc
    default String apply(Object input) throws IOException {
        var sb = new StringBuilder();
        this.apply(input, sb);
        return sb.toString();
    }
}
