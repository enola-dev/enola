/*
 * Copyright (c) 2017, 2018 Ericsson Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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
