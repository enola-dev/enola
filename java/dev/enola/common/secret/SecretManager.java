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
package dev.enola.common.secret;

import java.io.IOException;
import java.util.Optional;

/**
 * SecretManager is a "vault" of 🔑 {@link Secret}s.
 *
 * <p>You {@link #store(String, char[])}, then {@link #getOptional(String)} it; and maybe later
 * {@link #delete(String)} it again. There is very intentionally no <code>Set&lt;String&gt;
 * listKeys()</code> sort of method here.
 */
public interface SecretManager {

    // TODO: Implement a FileSecretManager which stores the secrets in an encrypted file vault which
    //   is decrypted with a passphrase which the user must type when starting an application is a
    //   possibility.  We, however, prefer focusing on implementations based on OS support.

    // TODO Write an ExecAgeSecretManager for https://github.com/Foxboron/age-plugin-tpm
    //   by generalizing the existing initial ExecPassSecretManager

    // TODO Write an AppleSecretManager
    //   Initially using https://github.com/remko/age-plugin-se
    //   Later see if there is some direct Java JCA for HSM API for this?

    // TODO Write a WindowsSecretManager

    // TODO Write CLI with ./enola secret store

    // TODO Write a multi-user dev.enola.common.context.TLC-based wrapper for this.

    /**
     * Stores a secret value associated with a unique key. The sensitive value is provided as a char
     * array. Implementations will zero out the input {@code value} array immediately after calling
     * this method for security. If a secret with the same key already exists, its value is
     * overwritten.
     *
     * @param key The unique key (name) for the secret.
     * @param value The sensitive secret value as a character array.
     * @throws IOException If an error occurs while storing the secret.
     */
    void store(String key, char[] value) throws IOException;

    /**
     * Retrieves a secret by its key, with optionality.
     *
     * @param key The unique key (name) of the secret to retrieve.
     * @return An {@link Optional} containing the {@link Secret}, if found.
     * @throws IOException If an error occurs while retrieving the secret.
     */
    Optional<Secret> getOptional(String key) throws IOException;

    /**
     * Retrieves a secret by its key; throws if not found.
     *
     * @param key The unique key (name) of the secret to retrieve.
     * @return An {@link Optional} containing the {@link Secret}, if found.
     * @throws IllegalStateException if the secret is not found.
     * @throws IOException If an error occurs while retrieving the secret.
     */
    default Secret get(String key) throws IllegalStateException, IOException {
        return getOptional(key)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        getClass().getSimpleName() + " missing secret: " + key));
    }

    /**
     * Deletes a secret from the manager.
     *
     * @param key The unique key (name) of the secret to delete.
     * @throws IOException If an error occurs while deleting the secret.
     */
    void delete(String key) throws IOException;
}
