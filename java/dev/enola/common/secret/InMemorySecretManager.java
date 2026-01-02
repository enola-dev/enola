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

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.ThreadSafe;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public class InMemorySecretManager implements SecretManager {

    private final Map<String, char[]> secrets = new ConcurrentHashMap<>();

    public InMemorySecretManager(Map<String, String> secrets) {
        secrets.forEach((key, value) -> this.secrets.put(key, value.toCharArray()));
    }

    public InMemorySecretManager(String key, String value) {
        this(ImmutableMap.of(key, value));
    }

    public InMemorySecretManager() {
        this(Map.of());
    }

    protected Map<String, char[]> getAll() {
        return Collections.unmodifiableMap(secrets);
    }

    @Override
    public void store(String name, char[] value) throws IOException {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret name cannot be null or empty.");
        }
        secrets.put(name, Arrays.copyOf(value, value.length));
        Arrays.fill(value, '\0');
    }

    @Override
    public Optional<Secret> getOptional(String name) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret name cannot be null or empty.");
        }
        var data = secrets.get(name);
        if (data == null) return Optional.empty();
        return Optional.of(new Secret(Arrays.copyOf(data, data.length)));
    }

    @Override
    public void delete(String key) throws IOException {
        char[] removedData = secrets.remove(key);
        if (removedData != null) Arrays.fill(removedData, '\0');
    }
}
