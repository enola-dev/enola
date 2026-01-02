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
package dev.enola.common.secret.context;

import dev.enola.common.context.TLC;
import dev.enola.common.secret.Secret;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.UnavailableSecretManager;

import java.io.IOException;
import java.util.Optional;

/**
 * SecretManagerTLC is a {@link SecretManager} implementation that looks up the current {@link
 * SecretManager} from the {@link TLC}. If it's not found there, then it falls back to one passed to
 * the constructor, which defaults to an {@link dev.enola.common.secret.UnavailableSecretManager}.
 */
public class SecretManagerTLC implements SecretManager {

    // We probably don't really need a Singleton<SecretManager> ...
    // public static final Singleton<SecretManager> SINGLETON = new Singleton<>() {};

    private final SecretManager fallback;

    /** Creates a new instance which falls back to the SecretManager passed to this constructor. */
    public SecretManagerTLC(SecretManager fallback) {
        this.fallback = fallback;
    }

    public SecretManagerTLC() {
        this(new UnavailableSecretManager());
    }

    @Override
    public void store(String key, char[] value) throws IOException {
        delegate().store(key, value);
    }

    @Override
    public Optional<Secret> getOptional(String key) throws IOException {
        return delegate().getOptional(key);
    }

    @Override
    public Secret get(String key) throws IllegalStateException, IOException {
        return delegate().get(key);
    }

    @Override
    public void delete(String key) throws IOException {
        delegate().delete(key);
    }

    private SecretManager delegate() {
        return TLC.optional(SecretManager.class).orElse(fallback);
    }
}
