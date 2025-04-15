/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import com.google.common.util.concurrent.UncheckedExecutionException;

import dev.enola.common.function.UncheckedInterruptedException;

import java.time.Duration;
import java.util.concurrent.*;

public final class CompletableFutures {
    private CompletableFutures() {}

    /**
     * Get CompletableFuture.
     *
     * @param future the CompletableFuture
     * @param timeout the maximum time to wait for the result
     * @return the result value
     * @throws TimeoutException if no value was set on the Future within the timeout
     * @throws RuntimeException if the
     * @throws UncheckedExecutionException
     * @throws UncheckedInterruptedException if an InterruptedException was caught
     */
    public static <T> T get(Future<T> future, Duration timeout)
            throws TimeoutException,
                    RuntimeException,
                    UncheckedInterruptedException,
                    UncheckedExecutionException {
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UncheckedInterruptedException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new UncheckedExecutionException(cause);
            }
        }
    }
}
