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
 * PrefixingSecretManager is a {@link SecretManager} that prefixes all keys with a given prefix.
 *
 * <p>This is useful, for example, to use a single {@link SecretManager} in a server environment for
 * multiple users (or any other Subject), or in a desktop environment to store application-specific
 * secrets on the user's desktop secret manager (like GNOME Keyring or macOS Keychain on Apple's
 * Secure Enclave).
 */
public class PrefixingSecretManager implements SecretManager {

    private final String prefix;
    private final SecretManager delegate;

    public PrefixingSecretManager(String prefix, SecretManager delegate) {
        this.prefix = prefix;
        this.delegate = delegate;
    }

    @Override
    public void store(String key, char[] value) throws IOException {
        delegate.store(prefix + key, value);
    }

    @Override
    public Optional<Secret> getOptional(String key) throws IOException {
        return delegate.getOptional(prefix + key);
    }

    @Override
    public void delete(String key) throws IOException {
        delegate.delete(prefix + key);
    }
}
