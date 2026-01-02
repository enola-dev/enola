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
package dev.enola.thing.java;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Resources;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.thing.Thing;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ThreadSafe
public class TypeToBuilder {

    // TODO Cache in @ThreadSafe (copy-on-write, perhaps?) Map

    @VisibleForTesting
    public record ThingAndBuilderClassPair(
            Class<Thing> thingClass, Class<Thing.Builder> builderClass) {}

    private static final ThingAndBuilderClassPair DEFAULT =
            new ThingAndBuilderClassPair(Thing.class, Thing.Builder.class);

    private static final ClassLoader classLoader = TypeToBuilder.class.getClassLoader();

    @VisibleForTesting
    public static ThingAndBuilderClassPair typeToBuilder(String typeIRI) {
        var resourceName = "META-INF/dev.enola/" + mangle(typeIRI);
        var url = classLoader.getResource(resourceName);
        if (url == null) return DEFAULT;

        String thingClassName;
        try {
            thingClassName = Resources.toString(url, StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            throw new UncheckedIOException(url.toString(), e);
        }
        String builderClassName = thingClassName + "$Builder";

        var builderClass = getClass(typeIRI, builderClassName, Thing.Builder.class);
        var thingClass = getClass(typeIRI, thingClassName, Thing.class);
        return new ThingAndBuilderClassPair(thingClass, builderClass);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(String typeIRI, String className, Class<T> baseClass) {
        try {
            var clazz = Class.forName(className);
            if (baseClass.isAssignableFrom(clazz)) return (Class<T>) clazz;
            else
                throw new IllegalStateException(
                        typeIRI + " -> " + className + " is not a " + baseClass.getName());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(className, e);
        }
    }

    private static String mangle(String typeIRI) {
        return typeIRI.replace('/', '-').replace(':', '_');
    }
}
