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
package dev.enola.model.enola.mediatype;

import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

import java.util.Set;

public interface HasFileExtensions extends Thing {

    @SuppressWarnings("unchecked")
    default @Nullable Set<String> fileExtensions() {
        return get("https://enola.dev/fileExtensions", Set.class);
    }

    interface Builder<B extends HasFileExtensions> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        default HasFileExtensions.Builder<B> addFileExtension(String fileExtension) {
            add("https://enola.dev/fileExtensions", fileExtension);
            return this;
        }

        default HasFileExtensions.Builder<B> addAllFileExtensions(Iterable<String> fileExtensions) {
            addAll("https://enola.dev/fileExtensions", fileExtensions);
            return this;
        }
    }
}
