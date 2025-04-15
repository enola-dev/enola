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
package dev.enola.common.function;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

/**
 * Wraps an InterruptedException as an unchecked (runtime) exception.
 *
 * @see java.io.UncheckedIOException
 * @see Threads#sleep(Duration)
 */
public class UncheckedInterruptedException extends RuntimeException {
    // TODO Move this class from dev.enola.common.function to dev.enola.common.concurrent!

    // TODO Get rid of this class? Take inspiration from Guava's Futures helpers.

    public UncheckedInterruptedException(InterruptedException cause) {
        super(requireNonNull(cause));
    }
}
