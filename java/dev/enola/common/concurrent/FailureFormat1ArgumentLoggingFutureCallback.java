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
final class FailureFormat1ArgumentLoggingFutureCallback<V>
        extends FailureLoggingFutureCallbackBase<V> {

    private final String format;
    private final Object arg;

    FailureFormat1ArgumentLoggingFutureCallback(Logger logger, String format, Object arg) {
        super(logger);
        this.format = "Future (eventually) failed: " + requireNonNull(format, "format is null");
        // do *NOT* null check this one (that's valid)
        this.arg = arg;
    }

    @Override
    public void onFailure(Throwable throwable) {
        getLogger().error(format, arg, throwable);
    }
}
