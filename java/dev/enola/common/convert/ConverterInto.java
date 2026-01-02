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

import com.google.common.io.ByteSink;
import com.google.common.io.CharSink;
import com.google.errorprone.annotations.CheckReturnValue;

import dev.enola.common.Builder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Converts an object of type I into an existing object of type O.
 *
 * <p>O is typically something like an {@link Appendable}, {@link Builder} (or any other such <code>
 * *Builder</code>), {@link ByteSink}, {@link CharSink}, {@link OutputStream}, or {@link Writer} or
 * some similar such kind of thing.
 *
 * <p>The "context" of the conversion, if any, must be part of I (or available via {@link
 * dev.enola.common.context.TLC}).
 *
 * @param <I> the type of input objects to convert from
 * @param <O> the type of output objects to convert into
 * @see Converter
 */
@FunctionalInterface
public interface ConverterInto<I, O> {

    /**
     * Convert from an I into an O.
     *
     * @param from the input to convert
     * @param into the destination where to output
     * @return true if conversion was successful, false if this converter cannot handle it; see
     *     {@link ConverterIntoChain}
     * @throws ConversionException in case of conversion problems or technical failures to read from
     *     I or write to O
     * @throws IOException TODO wrap IOException into ConversionException and remove throws
     */
    @CheckReturnValue // NB: Use convertIntoOrThrow() instead!
    boolean convertInto(I from, O into) throws ConversionException, IOException;

    default void convertIntoOrThrow(I from, O into) throws ConversionException {
        try {
            if (!convertInto(from, into)) {
                throw new ConversionException(
                        this + " could not convert " + from + " into " + into);
            }
        } catch (IOException e) {
            throw new ConversionException(
                    "Caught IOException while converting " + from + " to " + into, e);
        }
    }
}
