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

import java.util.concurrent.CancellationException;

/**
 * Failure logging future callback with a single String message.
 *
 * @author Michael Vorburger.ch, originally for https://www.opendaylight.org
 */
// package-local not public (for the time being)
final class FailureMessageLoggingFutureCallback<V>
        extends dev.enola.common.concurrent.FailureLoggingFutureCallbackBase<V> {
    private final String message;

    FailureMessageLoggingFutureCallback(Logger logger, String message) {
        super(logger);
        this.message = requireNonNull(message, "message is null");
    }

    @Override
    public void onFailure(Throwable throwable) {
        if (throwable instanceof CancellationException) {
            // CancellationException are (typically) no cause for alarm, and debug instead of error
            // level is enough
            // as these can happen during shutdown when we interrupt running threads, and should not
            // pollute logs.
            getLogger()
                    .debug(
                            "Future (eventually) failed with CancellationException: {}",
                            message,
                            throwable);
        } else {
            getLogger().error("Future (eventually) failed: {}", message, throwable);
        }
    }
}
