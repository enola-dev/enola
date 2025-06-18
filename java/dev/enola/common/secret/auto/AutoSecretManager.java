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

import static org.slf4j.LoggerFactory.getLogger;

import dev.enola.common.FreedesktopDirectories;
import dev.enola.common.exec.ExecPATH;
import dev.enola.common.secret.Secret;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.exec.ExecPassSecretManager;
import dev.enola.common.secret.yaml.YamlSecretManager;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class AutoSecretManager implements SecretManager {

    private static final Logger LOG = getLogger(AutoSecretManager.class);

    // main() just for quick demo / usage illustration
    public static void main(String[] args) throws IOException {
        var secretManager = new AutoSecretManager();
        // secretManager.store("test", "hello, world".toCharArray());
        secretManager.get("test").process(System.out::println);
    }

    private static Optional<SecretManager> yamlInExecPass() throws IOException {
        if (!ExecPATH.scan().containsKey("pass")) return Optional.empty();
        var id = "enola.dev"; // TODO Use (~) FreedesktopDirectories
        var TD = "Please create (empty) pass edit " + id;
        var pass = new ExecPassSecretManager(true);
        return Optional.of(
                new YamlSecretManager(
                        input -> pass.store(id, input.toCharArray()),
                        () ->
                                pass.getOptional(id)
                                        .orElseThrow(() -> new IOException(TD))
                                        .map(String::new)));
    }

    private final SecretManager delegate;

    public AutoSecretManager() throws IOException {
        // TODO Add support for GnomeSecretManager, using class DesktopDetector

        if (System.getenv("BAZEL_TEST") != null) {
            var azkaban = System.getenv("ENOLA.DEV_AZKABAN");
            if (azkaban == null) {
                this.delegate = new UnavailableSecretManager();
                LOG.warn("No Secrets! Set ENOLA.DEV_AZKABAN under BAZEL_TEST for test secrets.");
            } else {
                var azkabanPath = Path.of(azkaban);
                if (azkabanPath.toFile().exists()) {
                    this.delegate = new InsecureUnencryptedYamlFileSecretManager(azkabanPath);
                } else {
                    this.delegate = new UnavailableSecretManager();
                    LOG.warn("ENOLA.DEV_AZKABAN is set, but does not exist: " + azkabanPath);
                }
            }
        } else {
            var opt = yamlInExecPass();
            if (opt.isPresent()) this.delegate = opt.get();
            else {
                LOG.warn(
                        "The ExecPassSecretManager is N/A, so using"
                                + " InsecureUnencryptedYamlFileSecretManager");
                this.delegate =
                        new InsecureUnencryptedYamlFileSecretManager(
                                FreedesktopDirectories.PLAINTEXT_VAULT_FILE);
            }
        }
    }

    @Override
    public void store(String key, char[] value) throws IOException {
        delegate.store(key, value);
    }

    @Override
    public Optional<Secret> getOptional(String key) throws IOException {
        return delegate.getOptional(key);
    }

    @Override
    public void delete(String key) throws IOException {
        delegate.delete(key);
    }
}
