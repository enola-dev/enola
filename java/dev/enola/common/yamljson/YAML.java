/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.yamljson;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;

import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.ScalarStyle;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public final class YAML {

    private static Load newLoad() {
        // NB: No need for new SafeConstructor(), here; because that's
        // for org.yaml.snakeyaml, whereas this is for org.snakeyaml.engine.
        var loadSettings = LoadSettings.builder();
        // NB: Keep in-sync with similar (but not same, different API!) in YamlObjectReader
        loadSettings.setAllowDuplicateKeys(false);
        loadSettings.setAllowRecursiveKeys(false);
        loadSettings.setCodePointLimit(10 * 1024 * 1024); // 10 MB
        return new Load(loadSettings.build());
    }

    private static Map<?, ?> iterable2singleMap(Iterable<?> iterable) {
        var iterator = iterable.iterator();
        if (!iterator.hasNext()) throw new IllegalArgumentException("Empty YAML");
        var root = iterator.next();
        if (root instanceof Map<?, ?> map) {
            if (iterator.hasNext())
                throw new IllegalArgumentException("YAML with multiple --- documents");
            return map;
        } else throw new IllegalArgumentException("YAML document is not Map");
    }

    public static Iterable<?> read(String yaml) {
        return newLoad().loadAllFromString(yaml);
    }

    public static Map<?, ?> readSingleMap(String yaml) {
        return iterable2singleMap(read(yaml));
    }

    public static void read(ReadableResource yaml, Consumer<Iterable> itery) throws IOException {
        try (var is = yaml.byteSource().openBufferedStream()) {
            itery.accept(newLoad().loadAllFromInputStream(is));
        }
    }

    public static void readSingleMap(ReadableResource yaml, Consumer<Map<?, ?>> mappy)
            throws IOException {
        read(yaml, iterable -> mappy.accept(iterable2singleMap(iterable)));
    }

    public static String write(Object object) {
        DumpSettings settings =
                DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.PLAIN).build();
        Dump dump = new Dump(settings);
        return dump.dumpToString(object);
    }

    public static void write(Object object, WritableResource yaml) throws IOException {
        yaml.charSink().write(write(object));
    }

    private YAML() {}
}
