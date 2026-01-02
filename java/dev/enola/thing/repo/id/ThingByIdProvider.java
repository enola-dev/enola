/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.repo.id;

import dev.enola.thing.Thing;

// TODO Replace dev.enola.thing.repo.id with package dev.enola.data.id
public interface ThingByIdProvider { // extends Provider<ID, Thing> {

    // Thing get(String classID, Object[] ids);

    // NB: Implementations would use the ids_separator:
    <T extends Thing> T get(Class<T> clazz, String id);

    // <T extends Thing> T get(Class<T> clazz, Object id1);
    // <T extends Thing> T get(Class<T> clazz, Object id1, Object id2);
    // <T extends Thing> T get(Class<T> clazz, Object... ids);

    // @Override
    // default Thing get(ID id) {
    //    return get(id.classID(), id.ids());
    // }
}
