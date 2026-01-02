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

/**
 * Converts an object of type I to a new object of type O.
 *
 * <p>The "context" of the conversion, if any, must be part of I.
 *
 * <p>For #efficiency, consider whether this or a {@link ConverterInto} is more suitable.
 *
 * <p>Converter implementations "have" to convert, and should never return null; if they may or may
 * not convert depending on I, then implement an {@link OptionalConverter} instead of throwing a
 * ConversionException.
 *
 * <p>{@link BiConverter} is a bi-directional alternative to this.
 *
 * @param <I> the input type
 * @param <O> the output type
 */
@FunctionalInterface
public interface Converter<I, O> {

    O convert(I input) throws ConversionException;
}
