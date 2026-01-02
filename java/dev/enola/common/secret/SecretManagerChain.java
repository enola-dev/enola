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

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SecretManagerChain implements SecretManager {

    private final SecretManager primary;
    private final ImmutableList<SecretManager> secondaries;

    public SecretManagerChain(SecretManager primary, List<SecretManager> secondaries) {
        this.primary = requireNonNull(primary);
        this.secondaries = ImmutableList.copyOf(secondaries);
    }

    public SecretManagerChain(SecretManager primary, SecretManager... secondaries) {
        this(primary, ImmutableList.copyOf(secondaries));
    }

    public SecretManagerChain(List<SecretManager> managers) {
        this(
                requireNonNull(Iterables.getFirst(managers, new UnavailableSecretManager())),
                managers.subList(1, managers.size()));
    }

    @Override
    public void store(String key, char[] value) throws IOException {
        primary.store(key, value);
    }

    @Override
    public void delete(String key) throws IOException {
        primary.delete(key);
    }

    @Override
    public Optional<Secret> getOptional(String key) throws IOException {
        var opt = primary.getOptional(key);
        if (opt.isPresent()) return opt;

        for (var secondary : secondaries) {
            opt = secondary.getOptional(key);
            if (opt.isPresent()) return opt;
        }

        return Optional.empty();
    }
}
