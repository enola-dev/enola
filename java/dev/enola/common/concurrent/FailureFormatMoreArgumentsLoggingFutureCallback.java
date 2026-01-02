/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2017-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.concurrent;

import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;

/**
 * Failure logging future callback with a message format String and 1 argument.
 *
 * @author Michael Vorburger.ch, originally for https://www.opendaylight.org
 */
// package-local not public (for the time being)
final class FailureFormatMoreArgumentsLoggingFutureCallback<V>
        extends FailureLoggingFutureCallbackBase<V> {

    private final String format;
    private final Object[] arguments;

    FailureFormatMoreArgumentsLoggingFutureCallback(
            Logger logger, String format, Object... arguments) {
        super(logger);
        this.format = requireNonNull(format, "format is null");
        // do *NOT* null check these (it's valid)
        this.arguments = arguments;
    }

    @Override
    public void onFailure(Throwable throwable) {
        Object[] argumentsIncludingThrowable = new Object[arguments.length + 1];
        System.arraycopy(arguments, 0, argumentsIncludingThrowable, 0, arguments.length);
        argumentsIncludingThrowable[arguments.length] = throwable;

        getLogger().error("Future (eventually) failed: " + format, argumentsIncludingThrowable);
    }
}
