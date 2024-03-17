/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import dev.enola.common.io.resource.convert.CatchingResourceConverter;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.util.Iterator;
import java.util.Map;

public class YamlJson {

    public static final CatchingResourceConverter JSON_TO_YAML =
            (from, into) -> {
                into.charSink().write(jsonToYaml(from.charSource().read()));
                return true;
            };

    public static final CatchingResourceConverter YAML_TO_JSON =
            (from, into) -> {
                into.charSink().write(yamlToJson(from.charSource().read()));
                return true;
            };

    public static String jsonToYaml(String json) {
        Object object = JSON.readObject(json);

        if (object == null || object instanceof Map && ((Map<?, ?>) object).isEmpty()) {
            return "";
        }

        return YAML.write(object);
    }

    public static String yamlToJson(String yaml) {
        LoadSettings settings = LoadSettings.builder().build();
        Load load = new Load(settings);
        Iterable<Object> list = load.loadAllFromString(yaml);
        Iterator<Object> iter = list.iterator();

        if (!iter.hasNext()) {
            return "";
        } else {
            Object firstRoot = list.iterator().next();
            if (iter.hasNext()) {
                throw new IllegalArgumentException(
                        "YAML with more than 1 root cannot be converted to JSON");
            }
            return JSON.write(firstRoot);
        }
    }
}
