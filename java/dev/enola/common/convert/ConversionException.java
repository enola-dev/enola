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

import dev.enola.common.URILineColumnMessage;
import dev.enola.common.context.ContextualizedRuntimeException;

import java.net.URI;

/**
 * Failures encountered by {@link ConverterInto}, {@link Converter} and {@link BiConverter}
 * implementations.
 */
public class ConversionException extends ContextualizedRuntimeException {
    // TODO Re-try making this extends Exception instead Runtime, now that I have 👟 MoreStreams

    public ConversionException(String message, URI uri, long line, long column, Throwable cause) {
        super(new URILineColumnMessage(message, uri, line, column).format(), cause);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(String message) {
        super(message);
    }
}
