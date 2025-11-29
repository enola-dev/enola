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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import dev.enola.common.ObjectTreeSorter;

import java.time.Instant;
import java.util.Map;

/**
 * JSON utilities.
 *
 * @deprecated Use dev.enola.common.jackson.ObjectMappers instead.
 */
@Deprecated // TODO Replace with dev.enola.common.jackson.ObjectMappers and then remove GSON
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

    @Deprecated
    public static Object readObject(String json) {
        if (json.isEmpty()) return ""; // NOT Collections.emptyMap();
        return read(json, Object.class);
    }

    @Deprecated
    public static Map<String, Object> readMap(String json) {
        TypeToken<Map<String, Object>> mapType = new TypeToken<Map<String, Object>>() {};
        return read(json, mapType);
    }

    @Deprecated
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
    @Deprecated // ?
    @SuppressWarnings("rawtypes")
    // This is used by dev.enola.common.canonicalize.Canonicalizer
    // There is now very similar code in dev.enola.common.jackson.testlib.JsonTester
    public static String canonicalize(String json, boolean format) {
        // TODO Consider instead using
        // https://github.com/filip26/titanium-json-ld/blob/5c2c02c1f65b8e885fb689a460efba3f6925b479/src/main/java/com/apicatalog/jsonld/json/JsonCanonicalizer.java#L39
        return write(ObjectTreeSorter.sortByKeyIfMap(readObject(json)), format);
    }

    private JSON() {}
}
