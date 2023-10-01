/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package dev.enola.common.concurrent;

import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Thread's UncaughtExceptionHandler which logs to slf4j.
 *
 * @author Michael Vorburger.ch
 */
public final class LoggingThreadUncaughtExceptionHandler implements UncaughtExceptionHandler {

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
