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

import com.google.common.util.concurrent.FutureCallback;

import org.slf4j.Logger;

/**
 * Guava Future Callback which logs failures as errors abstract base class.
 *
 * @author Michael Vorburger.ch, originally for https://www.opendaylight.org
 */
// package-local not public (for the time being)
abstract class FailureLoggingFutureCallbackBase<V> implements FutureCallback<V> {

    private final Logger logger;

    FailureLoggingFutureCallbackBase(Logger logger) {
        this.logger = requireNonNull(logger, "logger is null");
    }

    @Override
    public abstract void onFailure(Throwable throwable);

    @Override
    public final void onSuccess(V result) {
        // do nothing
    }

    protected Logger getLogger() {
        return logger;
    }
}
