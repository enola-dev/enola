/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public final class JSON {
    private JSON() {}

    public static Object readObject(String json) {
        if ("".equals(json)) return ""; // NOT Collections.emptyMap();
        return new GsonBuilder().create().fromJson(json, Object.class);
    }

    public static Map<String, Object> readMap(String json) {
        TypeToken<Map<String, Object>> mapType = new TypeToken<Map<String, Object>>() {};
        return new GsonBuilder().create().fromJson(json, mapType);
    }

    public static String write(Object root) {
        if ("".equals(root)) return "";
        return new GsonBuilder().create().toJson(root);
    }

    /**
     * Canonicalize JSON, inspired by <a href="https://www.rfc-editor.org/rfc/rfc8785">RFC 8785</a>,
     * but not 100% fully compliant; because Java uses e.g. 1.0E30 instead of 1e+30, and a few other
     * such differences.
     *
     * @see dev.enola.common.canonicalize.Canonicalizer
     */
    @SuppressWarnings("rawtypes")
    public static String canonicalize(String json) {
        return write(sortByKeyIfMap(readObject(json)));
    }

    @SuppressWarnings("unchecked")
    private static Object sortByKeyIfMap(Object object) {
        if (object instanceof Map) {
            var map = (Map<String, Object>) object;
            return sortByKey(map);
        } else if (object instanceof List list) {
            // NB: We cannot use an ImmutableList here, because null is permitted!
            var newList = new ArrayList<>(list.size());
            for (var element : list) {
                var sorted = sortByKeyIfMap(element);
                newList.add(sorted);
            }
            return newList;
        }
        return object;
    }

    private static Map<String, Object> sortByKey(Map<String, Object> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);

        var newMap = ImmutableMap.<String, Object>builderWithExpectedSize(keys.size());
        for (var key : keys) {
            newMap.put(key, sortByKeyIfMap(map.get(key)));
        }
        return newMap.build();
    }
}
