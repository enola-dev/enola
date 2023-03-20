/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.ScalarStyle;

import java.util.Iterator;
import java.util.Map;

public class YamlJson {

    public static String jsonToYaml(String json) {
        TypeToken<Map<String, Object>> mapType = new TypeToken<Map<String, Object>>() {};
        Map<String, Object> map = new GsonBuilder().create().fromJson(json, mapType);

        if (map == null || map.isEmpty()) {
            return "";
        }

        DumpSettings settings =
                DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.PLAIN).build();
        Dump dump = new Dump(settings);
        return dump.dumpToString(map);
    }

    public static String yamlToJson(String yaml) {
        LoadSettings settings = LoadSettings.builder().build();
        Load load = new Load(settings);
        Iterable<Object> list = load.loadAllFromString(yaml);
        Iterator<Object> iter = list.iterator();

        if (!iter.hasNext()) {
            return ""; // Or "{}"?! Or "[]"?!
        } else {
            Object firstRoot = list.iterator().next();
            if (iter.hasNext()) {
                throw new IllegalArgumentException(
                        "YAML with more than 1 root cannot be converted to JSON");
            }
            return new GsonBuilder().create().toJson(firstRoot);
        }
    }
}
