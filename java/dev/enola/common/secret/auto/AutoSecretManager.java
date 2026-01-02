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

import dev.enola.common.FreedesktopDirectories;
import dev.enola.common.exec.ExecPATH;
import dev.enola.common.secret.*;
import dev.enola.common.secret.exec.ExecPassSecretManager;
import dev.enola.common.secret.yaml.YamlSecretManager;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AutoSecretManager offers an {@link #INSTANCE} of {@link SecretManager} which implements <a
 * href="https://docs.enola.dev/use/secret/">the logic described here</a>.
 */
public final class AutoSecretManager {

    private static @Nullable SecretManager INSTANCE;

    // main() just for quick demo / usage illustration
    public static void main(String[] args) throws IOException {
        var secretManager = AutoSecretManager.INSTANCE();
        // secretManager.store("test", "hello, world".toCharArray());
        secretManager.get("test").process(System.out::println);
    }

    private static Optional<SecretManager> yamlInExecPass() throws IOException {
        if (!ExecPATH.scan().containsKey("pass")) return Optional.empty();

        var id = "enola.dev"; // TODO Use (~) FreedesktopDirectories
        var homeDir = System.getProperty("user.home");
        if (homeDir == null) return Optional.empty();
        if (!new File(homeDir, ".password-store/" + id + ".gpg").exists()) return Optional.empty();

        var pass = new ExecPassSecretManager(true);
        return Optional.of(
                new YamlSecretManager(
                        input -> pass.store(id, input.toCharArray()),
                        () ->
                                pass.getOptional(id)
                                        .orElseThrow(() -> new IOException("No " + id))
                                        .map(String::new)));
    }

    public static synchronized SecretManager INSTANCE() {
        if (INSTANCE == null) {
            try {
                INSTANCE = create();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return INSTANCE;
    }

    private static SecretManager create() throws IOException {
        // TODO Add support for GnomeSecretManager, using class DesktopDetector

        List<SecretManager> secretManagers = new ArrayList<>();

        // TODO Move this into TestSecretManager?
        if (System.getenv("BAZEL_TEST") != null) {
            var azkaban = System.getenv("ENOLA.DEV_AZKABAN");
            if (azkaban != null) {
                var azkabanPath = Path.of(azkaban);
                if (azkabanPath.toFile().exists()) {
                    secretManagers.add(new InsecureUnencryptedYamlFileSecretManager(azkabanPath));
                }
            }
        }

        var pass = yamlInExecPass();
        if (pass.isPresent()) secretManagers.add(pass.get());
        else
            secretManagers.add(
                    new InsecureUnencryptedYamlFileSecretManager(
                            FreedesktopDirectories.PLAINTEXT_VAULT_FILE));

        secretManagers.add(new JavaPropertySecretManager());
        secretManagers.add(new EnvironmentSecretManager());
        return new SecretManagerChain(secretManagers);
    }

    private AutoSecretManager() {}
}
