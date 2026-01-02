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
package dev.enola.common.secret.auto;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.secret.SecretManager;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;

public class InsecureUnencryptedYamlFileSecretManagerTest {

    @Test
    public void newEmptyFile() throws IOException {
        var newFile = Files.createTempFile("InsecureUnencryptedYamlFileSecretManagerTest", ".yaml");
        SecretManager sm = new InsecureUnencryptedYamlFileSecretManager(newFile);
        assertThat(sm.getOptional("TEST")).isEmpty();
    }

    @Test
    public void newNonExistingFile() throws IOException {
        var newFile = Files.createTempFile("InsecureUnencryptedYamlFileSecretManagerTest", ".yaml");
        assertThat(newFile.toFile().delete()).isTrue();

        SecretManager sm = new InsecureUnencryptedYamlFileSecretManager(newFile);
        assertThat(sm.getOptional("TEST")).isEmpty();
    }
}
