/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

/**
 * {@code struct}ures of data.
 *
 * <p>Write an interface, and let tooling generate an immutable implementation and its builder.
 *
 * <p>There is, very intentionally, no parent / marker interface. Any object may be a "struct".
 *
 * <p>For I/O, use {@code dev.enola.common.io.object}. toString() is implemented for debugging.
 *
 * <p>Typical Java "primitive" (AKA "simple") types, like String, Integer, Boolean, etc. are
 * supported as properties. Implementations must support (immutable) custom types, not limit them to
 * something hard-coded.
 *
 * <p>Struct objects can totally contain single nested struct objects.
 *
 * <p>Struct objects can use {@link java.util.List}s - but no Arrays.
 *
 * <p>Struct objects can use {@link java.util.Set}, but I/O may consider them as List.
 *
 * <p>Struct objects can use {@link java.util.Map}s with String as keys and other Structs as values.
 *
 * <p>Struct objects should not use any other {@link java.lang.Iterable}s types.
 *
 * <p>null is not a valid entry for any container.
 *
 * <p>Validation is an entirely orthogonal concern.
 *
 * <p>Repository &amp; Query etc. is orthogonal.
 *
 * <p>hashCode() and equals() are correct.
 *
 * <p>Inheritance is possible.
 */
// TODO For Validation, use {@code dev.enola.common.io.validation}.
// TODO <p>See also dev.enola.common.thing (TBD).
// TODO Support "extra field properties" as Map.
@NullMarked
package dev.enola.common.struct;

import org.jspecify.annotations.NullMarked;
