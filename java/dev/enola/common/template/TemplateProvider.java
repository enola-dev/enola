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
package dev.enola.common.template;

import dev.enola.common.io.resource.ReadableResource;

import java.io.IOException;
import java.util.Optional;

/**
 * Provider of {@link Template}.
 *
 * <p>Implementations could be based on various frameworks, such as those listed on <a
 * href="https://github.com/akullpp/awesome-java?tab=readme-ov-file#template-engine">akullpp/awesome-java#template-engine</a>
 * or <a
 * href="https://github.com/sshailabh/awesome-template-engine?tab=readme-ov-file#java">sshailabh/awesome-template-engine</a>.
 */
public interface TemplateProvider {

    // TODO Write a CachingTemplateProvider

    Optional<Template> optional(ReadableResource source) throws IOException;

    default Template get(ReadableResource source) throws IOException {
        return optional(source)
                .orElseThrow(
                        () ->
                                new IOException(
                                        getClass().getSimpleName() + " cannot read " + source));
    }
}
