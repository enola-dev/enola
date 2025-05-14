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
package dev.enola.common.secret.auto;

import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.yaml.YamlSecretManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * InsecureUnencryptedYamlFileSecretManager is a {@link SecretManager} implementation that stores
 * secrets in an unencrypted YAML file. You should ideally really not use this in the real world.
 * It's included here as a "fallback" for autoconfiguration when no other SecretManager can be used.
 */
class InsecureUnencryptedYamlFileSecretManager extends YamlSecretManager {

    InsecureUnencryptedYamlFileSecretManager(Path path) throws IOException {
        super(yaml -> Files.writeString(path, yaml), () -> Files.readString(path));
    }
}
