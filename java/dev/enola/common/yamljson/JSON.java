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

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class JSON {

    private static GsonBuilder newBuilder() {
        return new GsonBuilder()
                .disableJdkUnsafe()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(Instant.class, new InstantTypeAdapter());
    }

    private static final Gson read = newBuilder().create();

    private static <T> T read(String json, Class<T> classOfT) {
        try {
            return read.fromJson(json, classOfT);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Failed to parse JSON: " + json, e);
        }
    }

    private static <T> T read(String json, TypeToken<T> typeOfT) {
        try {
            return read.fromJson(json, typeOfT);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Failed to parse JSON: " + json, e);
        }
    }

    public static Object readObject(String json) {
        if (json.isEmpty()) return ""; // NOT Collections.emptyMap();
        return read(json, Object.class);
    }

    public static Map<String, Object> readMap(String json) {
        TypeToken<Map<String, Object>> mapType = new TypeToken<Map<String, Object>>() {};
        return read(json, mapType);
    }

    public static String write(Object root, boolean format) {
        if ("".equals(root)) return "";
        var builder = newBuilder();
        if (format) builder.setPrettyPrinting();
        return builder.create().toJson(root);
    }

    /**
     * Canonicalize JSON, inspired by <a href="https://www.rfc-editor.org/rfc/rfc8785">RFC 8785</a>,
     * but not 100% fully compliant; because Java uses e.g. 1.0E30 instead of 1e+30, and a few other
     * such differences.
     */
    @SuppressWarnings("rawtypes")
    // This is used by dev.enola.common.canonicalize.Canonicalizer
    public static String canonicalize(String json, boolean format) {
        // TODO Consider instead using
        // https://github.com/filip26/titanium-json-ld/blob/5c2c02c1f65b8e885fb689a460efba3f6925b479/src/main/java/com/apicatalog/jsonld/json/JsonCanonicalizer.java#L39
        return write(sortByKeyIfMap(readObject(json)), format);
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
            // TODO Do this only when canonicalizing JSON-LD, not any JSON:
            sortListByID(newList);
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

    private static void sortListByID(List<Object> list) {
        Collections.sort(
                list,
                (o1, o2) -> {
                    // skipcq: JAVA-C1003
                    if (o1 instanceof Map m1 && o2 instanceof Map m2) {
                        var oid1 = m1.get("@id");
                        var oid2 = m2.get("@id");
                        // skipcq: JAVA-C1003
                        if (oid1 instanceof String id1 && oid2 instanceof String id2)
                            return id1.compareTo(id2);
                        return 0;
                    } else return 0;
                });
    }

    private JSON() {}
}
