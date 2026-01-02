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
package dev.enola.common.convert;

import org.jspecify.annotations.Nullable;

/**
 * Bi-directional alternative to {@link Converter}.
 *
 * <p>See also Guava's similar {@link com.google.common.base.Converter}.
 *
 * @param <A> the first type that can be converted to and from
 * @param <B> the second type that can be converted to and from
 */
public interface BiConverter<A, B> {

    @Nullable B convertTo(@Nullable A input) throws ConversionException;

    @Nullable A convertFrom(@Nullable B input) throws ConversionException;
}
