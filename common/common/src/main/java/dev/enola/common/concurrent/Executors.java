/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2017 - 2023 The Enola <https://enola.dev> Authors
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

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Additional factory and utility methods for executors.
 *
 * <p>Use this instead of {@link java.util.concurrent.Executors}, because it ensures that the
 * returned Executor uses a {@link ThreadFactory} that is named, has a logging
 * UncaughtExceptionHandler, and returns (Guava's) ListenableFuture.
 *
 * @author Michael Vorburger.ch, originally for https://www.opendaylight.org
 */
public final class Executors {

    public static final long DEFAULT_TIMEOUT_FOR_SHUTDOWN = 10;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT_FOR_SHUTDOWN = TimeUnit.SECONDS;

    private Executors() {
        // Hidden on purpose
    }

    /**
     * Creates a single thread executor with a {@link ThreadFactory} that uses the provided prefix
     * for its thread names and logs uncaught exceptions with the specified {@link Logger}.
     *
     * @param namePrefix Prefix for this executor thread names
     * @param logger Logger used to log uncaught exceptions
     * @return the newly created single-threaded Executor
     */
    public static ListeningExecutorService newListeningSingleThreadExecutor(
            String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.newSingleThreadExecutor(
                        createThreadFactory(namePrefix, logger)));
    }

    public static ListeningExecutorService newFixedThreadPool(
            int size, String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.newFixedThreadPool(
                        size, createThreadFactory(namePrefix, logger)));
    }

    public static ListeningExecutorService newListeningCachedThreadPool(
            String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.newCachedThreadPool(
                        createThreadFactory(namePrefix, logger)));
    }

    public static ListeningScheduledExecutorService newListeningSingleThreadScheduledExecutor(
            String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.unconfigurableScheduledExecutorService(
                        java.util.concurrent.Executors.newSingleThreadScheduledExecutor(
                                createThreadFactory(namePrefix, logger))));
    }

    public static ListeningScheduledExecutorService newListeningScheduledThreadPool(
            int corePoolSize, String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.newScheduledThreadPool(
                        corePoolSize, createThreadFactory(namePrefix, logger)));
    }

    public static void shutdownAndAwaitTermination(ExecutorService executorService) {
        MoreExecutors.shutdownAndAwaitTermination(
                executorService, DEFAULT_TIMEOUT_FOR_SHUTDOWN, DEFAULT_TIMEOUT_UNIT_FOR_SHUTDOWN);
    }

    private static ThreadFactory createThreadFactory(String namePrefix, Logger logger) {
        return ThreadFactoryProvider.create(namePrefix, logger);
    }
}
