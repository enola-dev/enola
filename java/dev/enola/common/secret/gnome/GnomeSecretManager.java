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
package dev.enola.common.secret.gnome;

import de.swiesend.secretservice.simple.SimpleCollection;

import dev.enola.common.secret.Secret;
import dev.enola.common.secret.SecretManager;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * GnomeSecretManager is a {@link SecretManager} implementation that uses the GNOME keyring via
 * D-Bus. (An alternative would be to "shell out" and execute the external "secret-tool" command.)
 */
public class GnomeSecretManager implements SecretManager, Closeable {

    // TODO This does not actually work; see https://github.com/swiesend/secret-service/issues/52

    private final SimpleCollection dbus;

    public GnomeSecretManager() throws IOException {
        if (!SimpleCollection.isGnomeKeyringAvailable())
            throw new IllegalStateException("Gnome keyring is not available.");
        this.dbus = new SimpleCollection();
    }

    @Override
    public void store(String key, char[] value) throws IOException {
        if (dbus.createItem(key, new ArrayCharSequence(value)) == null) {
            Arrays.fill(value, '\0');
            throw new IOException("Failed to store secret.");
        }
    }

    @Override
    public Optional<Secret> getOptional(String key) {
        char[] secret = dbus.getSecret(key);
        return secret != null ? Optional.of(new Secret(secret)) : Optional.empty();
    }

    @Override
    public void delete(String key) {
        dbus.deleteItem(key);
    }

    @Override
    public void close() {
        dbus.close();
    }

    public static void main(String[] args) throws IOException {
        try (SimpleCollection collection = new SimpleCollection()) {
            String item = collection.createItem("My Item", "secret");

            char[] actual = collection.getSecret(item);
            // assertEquals("secret", new String(actual));
            // assertEquals("My Item", collection.getLabel(item));

            collection.deleteItem(item);
        }

        var secret = "hello, world".toCharArray();
        var secretManager = new GnomeSecretManager();
        secretManager.store("test", secret);
        secretManager
                .get("test")
                .process(
                        it -> {
                            if (Arrays.compare(it, secret) != 0) throw new IllegalStateException();
                        });
        secretManager.delete("test");
        secretManager.close();
    }
}
