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
 * {@link ConverterInto} which catches any failures and wraps them into ConversionException.
 *
 * @param <I> the input type
 * @param <O> the output type
 */
public interface CatchingConverterInto<I, O> extends ConverterInto<I, O> {

    boolean convertIntoThrows(I from, O into) throws Exception;

    @Override
    default /* final */ boolean convertInto(I from, O into) throws ConversionException {
        try {
            return convertIntoThrows(from, into);
        } catch (Exception e) {
            if (e instanceof ConversionException) throw (ConversionException) e;
            else throw new ConversionException("I/O failed; from=" + from + ", into=" + into, e);
        }
    }
}
