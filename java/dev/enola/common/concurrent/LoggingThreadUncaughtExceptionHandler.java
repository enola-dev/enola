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

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Thread's UncaughtExceptionHandler which logs to slf4j.
 *
 * @author Michael Vorburger.ch, originally for https://www.opendaylight.org
 */
final class LoggingThreadUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private final Logger logger;

    private LoggingThreadUncaughtExceptionHandler(Logger logger) {
        this.logger = requireNonNull(logger, "logger");
    }

    /** Factory method to obtain an instance of this bound to the passed slf4j Logger. */
    public static UncaughtExceptionHandler toLogger(Logger logger) {
        return new LoggingThreadUncaughtExceptionHandler(logger);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        logger.error(
                "Thread terminated due to uncaught exception: {}", thread.getName(), throwable);
    }
}
