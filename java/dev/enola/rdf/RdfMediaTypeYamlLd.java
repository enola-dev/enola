/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.rdf;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/** MediaType of https://json-ld.github.io/yaml-ld/ and https://json-ld.github.io/yaml-ld/spec/ */
@AutoService(MediaTypeProvider.class)
public class RdfMediaTypeYamlLd implements MediaTypeProvider {

    public static final MediaType YAML_LD =
            MediaType.create("application", "ld+yaml").withCharset(StandardCharsets.UTF_8);

    @Override
    public Map<String, MediaType> extensionsToTypes() {
        // *.yamlld as per https://json-ld.github.io/yaml-ld/spec/#application-ld-yaml
        return ImmutableMap.of("yamlld", YAML_LD);
    }
}
