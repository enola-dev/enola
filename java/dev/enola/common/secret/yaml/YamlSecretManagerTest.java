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
package dev.enola.common.secret.yaml;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;

import dev.enola.common.io.resource.DataResource;

import org.junit.Test;

import java.io.IOException;

public class YamlSecretManagerTest {

    // TODO @Test empty()

    // TODO @Test store()

    @Test
    public void load() throws IOException {
        var resource = DataResource.of("secret1: do-not-tell", YAML_UTF_8);
        var secretManager = new YamlSecretManager(resource);
        try (var secret = secretManager.get("secret1")) {
            secret.process(it -> assertThat(it).isEqualTo("do-not-tell".toCharArray()));
        }
    }
}
