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
package dev.enola.thing.impl;

import com.google.common.base.MoreObjects;

import dev.enola.thing.Thing;

import java.util.Objects;

final class ThingHashCodeEqualsToString {

    static boolean equals(Thing thiz, Object obj) {
        if (obj == thiz) return true;
        // NO NEED: if (obj == null) return false;
        // NOT:     if (getClass() != obj.getClass()) return false;
        if (!(obj instanceof Thing other)) return false;
        return Objects.equals(thiz.iri(), other.iri())
                && Objects.equals(thiz.properties(), other.properties())
                && Objects.equals(thiz.datatypes(), other.datatypes());
    }

    static int hashCode(Thing thiz) {
        return Objects.hash(thiz.iri(), thiz.properties(), thiz.datatypes());
    }

    static String toString(Thing thiz) {
        return MoreObjects.toStringHelper(thiz)
                .add("iri", thiz.iri())
                .add("properties", thiz.properties())
                .add("datatypes", thiz.datatypes())
                .toString();
    }

    private ThingHashCodeEqualsToString() {}
}
