/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.classpath;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.Resources;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/** Additional methods which really should be in Guava's {@link Resources} class. */
public class MoreResources {

    public static String toString(String resourceName) {
        try {
            return Resources.toString(Resources.getResource(resourceName), UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(resourceName, e);
        }
    }

    public static List<String> listResources(String prefix) {
        try {
            return ClassPath.from(MoreResources.class.getClassLoader()).getResources().stream()
                    .map(ClassPath.ResourceInfo::getResourceName)
                    .filter(name -> name.startsWith(prefix))
                    .toList();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
