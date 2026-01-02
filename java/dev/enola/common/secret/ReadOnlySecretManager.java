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

public abstract class ReadOnlySecretManager implements SecretManager {

    @Override
    public final void store(String key, char[] value) throws IOException {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " is read-only.");
    }

    @Override
    public final void delete(String key) throws IOException {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " is read-only.");
    }
}
