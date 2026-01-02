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
package dev.enola.datatype;

import com.google.errorprone.annotations.Immutable;

import dev.enola.common.convert.ObjectToStringBiConverter;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.regex.Pattern;

@Immutable
public interface Datatype<T> {
    // NB: NOT extends Thing; there's a DatatypeThing for that!

    /** IRI of this datatype. Always present, never null or empty. */
    String iri();

    /** Regular Expression {@link Pattern} which text of this datatype matches. */
    Optional<Pattern> pattern();

    /** Converter from/to T &lt;=&gt; text. */
    ObjectToStringBiConverter<T> stringConverter();

    /** Converter from/to Object (expected to be of T) &lt;==&gt; text. Just for convenience. */
    @SuppressWarnings("unchecked")
    default ObjectToStringBiConverter<Object> stringConverterFromObject() {
        return (ObjectToStringBiConverter<Object>) stringConverter();
    }

    // BytesToObjectConverter<?> fromBytesConverter()

    // ObjectToBytesConverter<?> toBytesConverter();

    /**
     * {@link Type} in Java.
     *
     * @return Java Type of this datatype, if any. (It may be unknown or N/A.)
     */
    Optional<Class<T>> javaType();

    // TODO Optional<TypeToken<T>> javaTypeToken();

    DatatypeBuilder<T> child();
}
