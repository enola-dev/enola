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
package dev.enola.thing.impl;

import com.google.common.base.MoreObjects;

import dev.enola.thing.HasIRI;
import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

final class ThingHashCodeEqualsToString {

    static boolean equals(PredicatesObjects thiz, @Nullable Object obj) {
        if (obj == thiz) return true;
        if (obj == null) return false;
        // NOT: if (getClass() != obj.getClass()) return false;
        if (obj instanceof Thing) return false; // TODO is this a good idea? But what's better?
        if (!(obj instanceof ImmutablePredicatesObjects other)) return false;
        return Objects.equals(thiz.properties(), other.properties())
                && Objects.equals(thiz.datatypes(), other.datatypes());
    }

    static boolean equals(Thing thiz, @Nullable Object obj) {
        if (obj == thiz) return true;
        if (obj == null) return false;
        // NOT: if (getClass() != obj.getClass()) return false;
        if (!(obj instanceof Thing other)) return false;
        return Objects.equals(thiz.iri(), other.iri())
                && Objects.equals(thiz.properties(), other.properties())
                && Objects.equals(thiz.datatypes(), other.datatypes());
    }

    static int hashCode(PredicatesObjects thiz) {
        return Objects.hash(thiz.properties(), thiz.datatypes());
    }

    static int hashCode(Thing thiz) {
        return Objects.hash(thiz.iri(), thiz.properties(), thiz.datatypes());
    }

    static String toString(
            Object thiz, Map<String, Object> properties, Map<String, String> datatypes) {
        return MoreObjects.toStringHelper(thiz)
                .add("properties", properties)
                .add("datatypes", datatypes)
                .toString();
    }

    static String toString(
            HasIRI thiz, Map<String, Object> properties, Map<String, String> datatypes) {
        String iri;
        try {
            iri = thiz.iri();
        } catch (IllegalStateException e) {
            // MutableThing's iri() throws IllegalStateException
            iri = "UNSET";
        }
        return MoreObjects.toStringHelper(thiz)
                .add("iri", iri)
                .add("properties", properties)
                .add("datatypes", datatypes)
                .toString();
    }

    private ThingHashCodeEqualsToString() {}
}
