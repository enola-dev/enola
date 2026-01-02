/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.data;

import dev.enola.common.concurrent.Threads;
import dev.enola.common.function.CheckedRunnable;

import java.time.Duration;

public class OptimisticLockingExceptionRetrier {

    private final int maxRetries;
    private final Duration retryDelay;

    private OptimisticLockingExceptionRetrier(int maxRetries, Duration retryDelay) {
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
    }

    public OptimisticLockingExceptionRetrier() {
        // TODO Make configurable, somehow/somewhere... Java system properties, probably?
        this(7, Duration.ofMillis(100));
    }

    /**
     * Retry.
     *
     * @param action Action to try (and retry) running
     * @param <E> Exception supertype which that Action may throw
     * @throws E re-thrown the Action itself threw it
     * @throws OptimisticLockingException thrown if maximum retries have been reached
     */
    public <E extends Exception> void retry(CheckedRunnable<E> action)
            throws E, OptimisticLockingException {
        int attempts = 0;
        while (attempts <= maxRetries) {
            try {
                action.run();
                return;
            } catch (OptimisticLockingException ex) {
                attempts++;
                if (attempts <= maxRetries) {
                    // TODO Log? Hit some observability metric thing?
                    Threads.sleep(retryDelay);
                } else {
                    throw new OptimisticLockingException("Max retries exceeded", ex);
                }
            } catch (Exception ex) {
                // Re-throw all other exceptions immediately!
                throw ex;
            }
        }
        throw new IllegalStateException("BUG: This should never have been reached?!");
    }
}
