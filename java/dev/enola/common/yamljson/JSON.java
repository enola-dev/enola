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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class JSON {
    private JSON() {}

    public static Object readObject(String json) {
        return new GsonBuilder().create().fromJson(json, Object.class);
    }

    public static Map<String, Object> readMap(String json) {
        TypeToken<Map<String, Object>> mapType = new TypeToken<Map<String, Object>>() {};
        return new GsonBuilder().create().fromJson(json, mapType);
    }

    public static String write(Object root) {
        return new GsonBuilder().create().toJson(root);
    }

    // TODO normalize() should ideally do away with order differences by sorting map keys
    @SuppressWarnings("rawtypes")
    public static String normalize(String json) {
        return write(sortByKeyIfMap(readObject(json)));
    }

    @SuppressWarnings("unchecked")
    private static Object sortByKeyIfMap(Object object) {
        if (object instanceof Map) {
            var map = (Map<String, Object>) object;
            return sortByKey(map);
        } else if (object instanceof List) {
            var list = (List) object;
            var newList = ImmutableList.builderWithExpectedSize(list.size());
            for (var element : list) {
                newList.add(sortByKeyIfMap(element));
            }
            return newList.build();
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
