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
package dev.enola.common.secret.yaml;

import dev.enola.common.function.CheckedConsumer;
import dev.enola.common.function.CheckedSupplier;
import dev.enola.common.secret.InMemorySecretManager;
import dev.enola.common.yamljson.YAML;

import java.io.IOException;
import java.io.UncheckedIOException;

public class YamlSecretManager extends InMemorySecretManager {

    private final CheckedConsumer<String, IOException> saveConsumer;
    private final CheckedSupplier<String, IOException> loadSupplier;

    public YamlSecretManager(
            CheckedConsumer<String, IOException> saveConsumer,
            CheckedSupplier<String, IOException> loadSupplier)
            throws IOException {
        this.saveConsumer = saveConsumer;
        this.loadSupplier = loadSupplier;
        load();
    }

    protected void load() throws IOException {
        var map = YAML.readSingleMap(loadSupplier.get());
        map.forEach(
                (key, value) -> {
                    if (key instanceof String keyAsString
                            && value instanceof String valueAsString) {
                        try {
                            super.store(keyAsString, valueAsString.toCharArray());
                        } catch (IOException e) {
                            throw new UncheckedIOException(
                                    "InMemorySecretManager should never throw IOException?!", e);
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Unsupported key/value types: "
                                        + key.getClass()
                                        + " / "
                                        + value.getClass());
                    }
                });
    }

    protected void save() throws IOException {
        saveConsumer.accept(YAML.write(super.getAll()));
    }

    @Override
    public void store(String key, char[] value) throws IOException {
        super.store(key, value);
        save();
    }

    @Override
    public void delete(String key) throws IOException {
        super.delete(key);
        save();
    }
}
