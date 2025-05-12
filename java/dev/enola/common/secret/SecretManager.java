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
package dev.enola.common.secret;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * SecretManager is a "vault" of 🔑 {@link Secret}s.
 *
 * <p>You {@link #store(String, char[])}, then {@link #getOptional(String)} it; and maybe later
 * {@link #delete(String)} it again. There is very intentionally no <tt>Set&lt;String&gt;
 * listKeys()</tt> sort of method here.
 */
public interface SecretManager {

    /**
     * Stores a secret value associated with a unique key. The sensitive value is provided as a char
     * array. Implementations will zero out the input {@code value} array immediately after calling
     * this method for security. If a secret with the same key already exists, its value is
     * overwritten.
     *
     * @param key The unique key (name) for the secret.
     * @param value The sensitive secret value as a character array.
     */
    void store(String key, char @Nullable [] value);

    /**
     * Retrieves a secret by its key, with optionality.
     *
     * @param key The unique key (name) of the secret to retrieve.
     * @return An {@link Optional} containing the {@link Secret}, if found.
     */
    Optional<Secret> getOptional(String key);

    /**
     * Retrieves a secret by its key; throws if not found.
     *
     * @param key The unique key (name) of the secret to retrieve.
     * @return An {@link Optional} containing the {@link Secret}, if found.
     * @throws IllegalStateException if the secret is not found.
     */
    default Secret get(String key) throws IllegalStateException {
        return getOptional(key)
                .orElseThrow(() -> new IllegalStateException("Secret missing: " + key));
    }

    /**
     * Deletes a secret from the manager.
     *
     * @param key The unique key (name) of the secret to delete.
     */
    void delete(String key);
}
