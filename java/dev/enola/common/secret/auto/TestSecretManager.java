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

import dev.enola.common.secret.ReadOnlySecretManager;
import dev.enola.common.secret.Secret;
import dev.enola.common.secret.SecretManager;

import java.io.IOException;
import java.util.Optional;

public class TestSecretManager extends ReadOnlySecretManager {

    private final SecretManager delegate;

    public TestSecretManager() {
        // TODO Move code from AutoSecretManager to here...
        delegate = AutoSecretManager.INSTANCE();
    }

    @Override
    public Optional<Secret> getOptional(String key) throws IOException {
        return delegate.getOptional(key);
    }
}
