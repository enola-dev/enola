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
package dev.enola.common.jackson.testlib;

import com.google.common.truth.Truth;

import dev.enola.common.ObjectTreeSorter;
import dev.enola.common.jackson.ObjectMappers;

public final class JsonTester {
    private JsonTester() {}

    private static String canonicalize(String json) throws Exception {
        // TODO Consider instead using
        // https://github.com/filip26/titanium-json-ld/blob/5c2c02c1f65b8e885fb689a460efba3f6925b479/src/main/java/com/apicatalog/jsonld/json/JsonCanonicalizer.java#L39

        var actualObject = ObjectMappers.JSON.readValue(json, Object.class);
        var canonicalizedObject = ObjectTreeSorter.sortByKeyIfMap(actualObject);
        var canonicalizedJSON = ObjectMappers.JSON.writeValueAsString(canonicalizedObject);

        return canonicalizedJSON;
    }

    public static void assertEqualsTo(String actualJSON, String expectedJSON) throws Exception {
        var canonicalizedActualJSON = canonicalize(actualJSON);
        var canonicalizedExpectedJSON = canonicalize(expectedJSON);
        Truth.assertThat(canonicalizedActualJSON).isEqualTo(canonicalizedExpectedJSON);
    }

    public static void assertEqualsTo(Object object, String expectedJSON) throws Exception {
        var actualJSON = ObjectMappers.JSON.writeValueAsString(object);
        assertEqualsTo(actualJSON, expectedJSON);
    }
}
