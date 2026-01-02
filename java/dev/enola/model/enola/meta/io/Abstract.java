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
package dev.enola.model.enola.meta.io;

import dev.enola.model.enola.meta.Class;
import dev.enola.model.enola.meta.Datatype;
import dev.enola.model.enola.meta.Property;
import dev.enola.model.enola.meta.Schema;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.id.ThingByIdProvider;

import java.util.Map;

abstract class Abstract implements ThingByIdProvider {

    abstract Map<String, Schema> schemas();

    abstract Map<String, Datatype> datatypes();

    abstract Map<String, Property> properties();

    abstract Map<String, Class> classes();

    @Override
    public <T extends Thing> T get(java.lang.Class<T> clazz, String id) {
        if (clazz.equals(Schema.class)) return (T) schemas().get(id);
        if (clazz.equals(Datatype.class)) return (T) datatypes().get(id);
        if (clazz.equals(Property.class)) return (T) properties().get(id);
        if (clazz.equals(Class.class)) return (T) classes().get(id);
        throw new IllegalArgumentException("Unknown class ID: " + clazz);
    }

    private Thing get(String classID, Object[] ids) {
        if (classID.equals(Schema.CLASS_IRI)) return get(Schema.class, ids);
        if (classID.equals(Datatype.CLASS_IRI)) return get(Datatype.class, ids);
        if (classID.equals(Property.CLASS_IRI)) return get(Property.class, ids);
        if (classID.equals(Class.CLASS_IRI)) return get(Class.class, ids);
        throw new IllegalArgumentException("Unknown class ID: " + classID);
    }

    private <T extends Thing> T get(java.lang.Class<T> clazz, Object id) {
        if (clazz.equals(Schema.class)) return (T) schemas().get(id.toString());
        throw new IllegalArgumentException("Class needs longer ID: " + clazz);
    }

    private <T extends Thing> T get(java.lang.Class<T> clazz, Object id1, Object id2) {
        // TODO Optimize implementation to avoid this (constant...) string concatenation...
        if (clazz.equals(Datatype.class)) return (T) datatypes().get(id1 + "." + id2);
        if (clazz.equals(Property.class)) return (T) properties().get(id1 + "." + id2);
        if (clazz.equals(Class.class)) return (T) classes().get(id1 + "." + id2);
        throw new IllegalArgumentException("Class needs another ID: " + clazz);
    }

    private <T extends Thing> T get(java.lang.Class<T> clazz, Object... ids) {
        if (clazz.equals(Schema.class) && ids.length == 1) return get(clazz, ids[0]);
        if (clazz.equals(Datatype.class) && ids.length == 2) return get(clazz, ids[0], ids[1]);
        if (clazz.equals(Property.class) && ids.length == 2) return get(clazz, ids[0], ids[1]);
        if (clazz.equals(Class.class) && ids.length == 2) return get(clazz, ids[0], ids[1]);
        throw new IllegalArgumentException(
                "Class & required ID Length don't match: " + clazz + ", " + ids.length);
    }
}
