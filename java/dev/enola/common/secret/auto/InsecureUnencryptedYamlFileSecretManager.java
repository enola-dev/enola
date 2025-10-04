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

import dev.enola.common.function.CheckedSupplier;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.yaml.YamlSecretManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * InsecureUnencryptedYamlFileSecretManager is a {@link SecretManager} implementation that stores
 * secrets in an unencrypted YAML file. You should ideally really not use this in the real world.
 * It's used by {@link AutoSecretManager} as a "fallback" for auto-configuration when no other
 * SecretManager can be used.
 *
 * <p>As a precaution, it checks that the file permissions are set to be readable and writeable only
 * by the user, but not their primary group, or even world (chmod 600). The implementation of this
 * check is theoretically vulnerable to Time-of-check to time-of-use (TOCTOU) race conditions, where
 * an attacker could change the file permissions between this check and the subsequent file
 * read/write operation, but it is good enough for this purpose.
 */
public class InsecureUnencryptedYamlFileSecretManager extends YamlSecretManager {

    private static final Logger LOG =
            LoggerFactory.getLogger(InsecureUnencryptedYamlFileSecretManager.class);

    InsecureUnencryptedYamlFileSecretManager(Path path) throws IOException {
        super(
                yaml -> {
                    checkOnlyUserNotGroupOrEvenWorldCanRead(path);
                    Files.writeString(path, yaml);
                },
                readOrEmpty(path));
    }

    private static CheckedSupplier<String, IOException> readOrEmpty(Path path) {
        return () -> {
            checkOnlyUserNotGroupOrEvenWorldCanRead(path);
            LOG.info("Read {}", path);
            if (path.toFile().exists()) return Files.readString(path);
            else return "";
        };
    }

    private static void checkOnlyUserNotGroupOrEvenWorldCanRead(Path path) throws IOException {
        if (!path.toFile().exists()) return;
        if (!path.getFileSystem().supportedFileAttributeViews().contains("posix")) return;

        var expected = PosixFilePermissions.fromString("rw-------"); // 600
        var actual = Files.getPosixFilePermissions(path);
        if (!actual.equals(expected))
            throw new IOException(
                    "Insecure file permissions "
                            + PosixFilePermissions.toString(actual)
                            + ", please fix with: chmod 600 "
                            + path);
    }
}
