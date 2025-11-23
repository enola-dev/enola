/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// TODO Move ObjectTreeSorter to a (TBD) dev.enola.common.testlib testonly module
public final class ObjectTreeSorter {
    private ObjectTreeSorter() {}

    @SuppressWarnings("unchecked")
    public static Object sortByKeyIfMap(Object object) {
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
}
