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

import java.io.IOException;
import java.util.Optional;

/**
 * SecretManager that reads secrets from the value of named environment variables.
 *
 * <p>This is not secure at all, and other implementations are preferred - but sometimes this is
 * still useful.
 */
public class EnvironmentSecretManager extends ReadOnlySecretManager {

    // TODO Use in AutoSecretManager, and document on https://docs.enola.dev/use/secret/

    @Override
    @SuppressWarnings("deprecation")
    public Optional<Secret> getOptional(String key) throws IOException {
        String value = System.getenv(key);
        return Optional.ofNullable(value).map(Secret::new);
    }
}
