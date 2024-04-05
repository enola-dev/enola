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
package dev.enola.datatype;

import dev.enola.common.convert.ObjectToStringBiConverter;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.regex.Pattern;

public interface Datatype<T> /* TODO extends Thing */ {

    /** IRI of this datatype. Always present, never null or empty. */
    String iri();

    /** Regular Expression {@link Pattern} which text of this datatype matches. */
    Optional<Pattern> pattern();

    /** Converter from/to text. */
    ObjectToStringBiConverter<T> stringConverter();

    // BytesToObjectConverter<?> fromBytesConverter()

    // ObjectToBytesConverter<?> toBytesConverter();

    /**
     * {@link Type} in Java.
     *
     * @return Java Type of this datatype, if any. (It may be unknown or N/A.)
     */
    Optional<Class<T>> javaType();
}
