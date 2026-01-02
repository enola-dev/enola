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
package dev.enola.common.concurrent;

import com.google.common.util.concurrent.Uninterruptibles;

import java.time.Duration;

public final class Threads {

    /**
     * Sleep 😴 for a certain duration.
     *
     * <p>This is a wrapper around {@link Thread#sleep(Duration)} which (correctly) handles its
     * {@link InterruptedException} by (re-)interrupting the current thread, and then re-throwing it
     * as a checked exception. It also checks for negative duration (which the original method just
     * ignores, which could hide bugs), and has an optimizing shortcut for duration 0.
     *
     * <p>See <a href="https://www.baeldung.com/java-interrupted-exception">Baeldung's related
     * article</a>, or <a
     * href="https://www.yegor256.com/2015/10/20/interrupted-exception.html">yegor256.com Blog
     * Post</a> and <a href="https://github.com/google/guava/issues/1219">Google Guava Issue
     * #1219</a>, as well as {@link Uninterruptibles#sleepUninterruptibly(Duration)} (which does
     * something different from this).
     *
     * @param duration Duration to sleep
     * @throws UncheckedInterruptedException if interrupted
     * @throws IllegalArgumentException if duration is negative
     */
    public static void sleep(Duration duration) {
        if (duration.isNegative())
            throw new IllegalArgumentException(duration + " cannot be negative");

        if (duration.isZero()) return;

        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UncheckedInterruptedException(e);
        }
    }

    private Threads() {}
}
