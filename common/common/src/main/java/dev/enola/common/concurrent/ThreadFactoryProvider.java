/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package dev.enola.common.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.slf4j.Logger;

import java.util.concurrent.ThreadFactory;

/**
 * Builder for {@link ThreadFactory}. Easier to use than the {@link
 * com.google.common.util.concurrent.ThreadFactoryBuilder}, because it enforces setting all required
 * properties through a staged builder.
 *
 * @author Michael Vorburger.ch
 */
public abstract class ThreadFactoryProvider {
    private ThreadFactoryProvider() {}

    /**
     * Creates a new {@link ThreadFactory}.
     *
     * @param namePrefix Prefix for threads from this factory. For example, "rpc-pool", to create *
     *     "rpc-pool-1/2/3" named threads. Note that this is a prefix, not a format, * so you pass
     *     just "rpc-pool" instead of e.g. "rpc-pool-%d".
     * @param logger Logger used to log uncaught exceptions from new threads created via this
     *     factory.
     * @return new ThreadFactory
     */
    public static ThreadFactory create(
            String namePrefix, Logger logger) { // Optional<Integer> priority
        ThreadFactoryBuilder guavaBuilder =
                new ThreadFactoryBuilder()
                        .setNameFormat(namePrefix + "-%d")
                        .setUncaughtExceptionHandler(
                                dev.enola.common.concurrent.LoggingThreadUncaughtExceptionHandler
                                        .toLogger(logger))
                        .setDaemon(true);
        // priority.ifPresent(guavaBuilder::setPriority);
        logger.info("ThreadFactory created: {}", namePrefix);
        return guavaBuilder.build();
    }
}
