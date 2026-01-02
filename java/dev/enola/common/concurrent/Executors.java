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

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import dev.enola.common.context.ContextAwareThreadFactory;

import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Additional factory and utility methods for executors.
 *
 * <p>Use this instead of {@link java.util.concurrent.Executors}, because it ensures that:
 *
 * <ul>
 *   <li>the {@link dev.enola.common.context.TLC} is correctly propagated
 *   <li>the returned Executor uses a {@link ThreadFactory} that is named,
 *   <li>has an UncaughtExceptionHandler which logs to SLF4j
 *   <li>can return (Guava's) ListenableFuture.
 * </ul>
 *
 * @author Michael Vorburger.ch (originally for <a
 *     href="https://www.opendaylight.org">OpenDaylight</a>)
 */
public final class Executors {

    // TODO Simplify and keep only ListeningExecutorService variants

    // TODO Use (only?) java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()
    // see
    // https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-6444CF1A-FCAD-4F8A-877F-4A72AA0143B7

    public static final long DEFAULT_TIMEOUT_FOR_SHUTDOWN = 10;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT_FOR_SHUTDOWN = TimeUnit.SECONDS;

    /**
     * Creates an executor service that runs each task in the thread that invokes execute/submit.
     * This does not take namePrefix and Logger arguments, to make it clear they are not needed,
     * because no separate new threads will ever be created. Note that a "scheduled direct Executor"
     * variant doesn't really make sense conceptually (unless you would want it to just completely
     * ignore all requested delays; which would be weird).
     *
     * @see MoreExecutors#newDirectExecutorService()
     */
    public static ListeningExecutorService newListeningDirectExecutor() {
        return MoreExecutors.newDirectExecutorService();
    }

    /**
     * Creates a single thread executor with a {@link ThreadFactory} that uses the provided prefix
     * for its thread names and logs uncaught exceptions with the specified {@link Logger}.
     *
     * @param namePrefix Prefix for threads from this executor. For example, "rpc-pool", to create
     *     "rpc-pool-1/2/3" named threads. Note that this is a prefix, not a format, so you pass
     *     just "rpc-pool" instead of e.g. "rpc-pool-%d".
     * @param logger Logger used to log uncaught exceptions from new threads created from this.
     * @see java.util.concurrent.Executors#newSingleThreadExecutor()
     */
    public static ListeningExecutorService newListeningSingleThreadExecutor(
            String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.newSingleThreadExecutor(
                        createThreadFactory(namePrefix, logger)));
    }

    public static ExecutorService newSingleThreadExecutor(String namePrefix, Logger logger) {
        return java.util.concurrent.Executors.newSingleThreadExecutor(
                createThreadFactory(namePrefix, logger));
    }

    /**
     * @see java.util.concurrent.Executors#newFixedThreadPool(int)
     */
    public static ListeningExecutorService newListeningFixedThreadPool(
            int size, String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.newFixedThreadPool(
                        size, createThreadFactory(namePrefix, logger)));
    }

    public static ExecutorService newFixedThreadPool(int size, String namePrefix, Logger logger) {
        return java.util.concurrent.Executors.newFixedThreadPool(
                size, createThreadFactory(namePrefix, logger));
    }

    /**
     * @see java.util.concurrent.Executors#newCachedThreadPool()
     */
    public static ListeningExecutorService newListeningCachedThreadPool(
            String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.newCachedThreadPool(
                        createThreadFactory(namePrefix, logger)));
    }

    public static ExecutorService newCachedThreadPool(String namePrefix, Logger logger) {
        return java.util.concurrent.Executors.newCachedThreadPool(
                createThreadFactory(namePrefix, logger));
    }

    /**
     * @see java.util.concurrent.Executors#newSingleThreadScheduledExecutor()
     */
    public static ListeningScheduledExecutorService newListeningSingleThreadScheduledExecutor(
            String namePrefix, Logger logger) {
        return MoreExecutors.listeningDecorator(
                java.util.concurrent.Executors.unconfigurableScheduledExecutorService(
                        java.util.concurrent.Executors.newSingleThreadScheduledExecutor(
                                createThreadFactory(namePrefix, logger))));
    }

    /**
     * @see java.util.concurrent.Executors#newScheduledThreadPool(int)
     */
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
        ThreadFactoryBuilder guavaBuilder =
                new ThreadFactoryBuilder()
                        .setNameFormat(namePrefix + "-%d")
                        .setUncaughtExceptionHandler(
                                LoggingThreadUncaughtExceptionHandler.toLogger(logger))
                        .setDaemon(true)
                        .setThreadFactory(new ContextAwareThreadFactory());
        // priority.ifPresent(guavaBuilder::setPriority);
        // logger.info("ThreadFactory created: {}", namePrefix);
        return guavaBuilder.build();
    }

    private Executors() {}
}
