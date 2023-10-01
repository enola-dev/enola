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

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import org.slf4j.Logger;

import java.util.concurrent.Future;

/**
 * Utility methods to add completion/failure logging to various kinds of Futures.
 *
 * @author Michael Vorburger.ch, originally for https://www.opendaylight.org
 */
@Beta
public final class LoggingFutures {
    private LoggingFutures() {}

    /**
     * Adds a callback to a Future which logs any failures.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param message message to log
     * @return ListenableFuture backed by the supplied future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> ListenableFuture<V> addErrorLogging(
            Future<V> future, Logger logger, String message) {
        return addErrorLogging(JdkFutureAdapters.listenInPoolThread(future), logger, message);
    }

    /**
     * Adds a callback to a Future which logs any failures.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param format format string conforming to {@link String#format(String, Object...)}
     * @param arg single format argument
     * @return ListenableFuture backed by the supplied future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> ListenableFuture<V> addErrorLogging(
            Future<V> future, Logger logger, String format, Object arg) {
        return addErrorLogging(JdkFutureAdapters.listenInPoolThread(future), logger, format, arg);
    }

    /**
     * Adds a callback to a ListenableFuture which logs any failures.
     *
     * <p>Instead of using this helper, you should consider directly using {@link
     * Futures#addCallback(ListenableFuture, FutureCallback, java.util.concurrent.Executor)} to add
     * a callback which does real error recovery in case of a failure instead of just logging an
     * error, if you can.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param format format string conforming to {@link String#format(String, Object...)}
     * @param args format arguments
     * @return ListenableFuture backed by the supplied future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> ListenableFuture<V> addErrorLogging(
            Future<V> future, Logger logger, String format, Object... args) {
        return addErrorLogging(JdkFutureAdapters.listenInPoolThread(future), logger, format, args);
    }

    /**
     * Adds a callback to a ListenableFuture which logs any failures.
     *
     * <p>Instead of using this helper, you should consider directly using {@link
     * Futures#addCallback(ListenableFuture, FutureCallback, java.util.concurrent.Executor)} to add
     * a callback which does real error recovery in case of a failure instead of just logging an
     * error, if you can.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param message message to log
     * @return The future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> ListenableFuture<V> addErrorLogging(
            ListenableFuture<V> future, Logger logger, String message) {
        return addCallback(
                future,
                new dev.enola.common.concurrent.FailureMessageLoggingFutureCallback<>(
                        logger, message));
    }

    /**
     * Adds a callback to a ListenableFuture which logs any failures.
     *
     * <p>Instead of using this helper, you should consider directly using {@link
     * Futures#addCallback(ListenableFuture, FutureCallback, java.util.concurrent.Executor)} to add
     * a callback which does real error recovery in case of a failure instead of just logging an
     * error, if you can.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param format format string conforming to {@link String#format(String, Object...)}
     * @param arg single format argument
     * @return The future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> ListenableFuture<V> addErrorLogging(
            ListenableFuture<V> future, Logger logger, String format, Object arg) {
        return addCallback(
                future,
                new dev.enola.common.concurrent.FailureFormat1ArgumentLoggingFutureCallback<V>(
                        logger, format, arg));
    }

    /**
     * Adds a callback to a ListenableFuture which logs any failures.
     *
     * <p>Instead of using this helper, you should consider directly using {@link
     * Futures#addCallback(ListenableFuture, FutureCallback, java.util.concurrent.Executor)} to add
     * a callback which does real error recovery in case of a failure instead of just logging an
     * error, if you can.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param format format string conforming to {@link String#format(String, Object...)}
     * @param args format arguments
     * @return The future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> ListenableFuture<V> addErrorLogging(
            ListenableFuture<V> future, Logger logger, String format, Object... args) {
        return addCallback(
                future,
                new dev.enola.common.concurrent.FailureFormatMoreArgumentsLoggingFutureCallback<V>(
                        logger, format, args));
    }

    /**
     * Adds a callback to a ListenableFuture which logs any failures.
     *
     * <p>Instead of using this helper, you should consider directly using {@link
     * Futures#addCallback(ListenableFuture, FutureCallback, java.util.concurrent.Executor)} to add
     * a callback which does real error recovery in case of a failure instead of just logging an
     * error, if you can.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param message message to log
     * @return The future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> FluentFuture<V> addErrorLogging(
            FluentFuture<V> future, Logger logger, String message) {
        return addCallback(
                future,
                new dev.enola.common.concurrent.FailureMessageLoggingFutureCallback<>(
                        logger, message));
    }

    /**
     * Adds a callback to a ListenableFuture which logs any failures.
     *
     * <p>Instead of using this helper, you should consider directly using {@link
     * Futures#addCallback(ListenableFuture, FutureCallback, java.util.concurrent.Executor)} to add
     * a callback which does real error recovery in case of a failure instead of just logging an
     * error, if you can.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param format format string conforming to {@link String#format(String, Object...)}
     * @param arg single format argument
     * @return The future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> FluentFuture<V> addErrorLogging(
            FluentFuture<V> future, Logger logger, String format, Object arg) {
        return addCallback(
                future,
                new dev.enola.common.concurrent.FailureFormat1ArgumentLoggingFutureCallback<>(
                        logger, format, arg));
    }

    /**
     * Adds a callback to a FluentFuture which logs any failures.
     *
     * <p>Instead of using this helper, you should consider directly using {@link
     * Futures#addCallback(ListenableFuture, FutureCallback, java.util.concurrent.Executor)} to add
     * a callback which does real error recovery in case of a failure instead of just logging an
     * error, if you can.
     *
     * @param future the future to add logging to
     * @param logger logger to use
     * @param format format string conforming to {@link String#format(String, Object...)}
     * @param args format arguments
     * @return The future
     * @throws NullPointerException if any of the arguments is null
     */
    public static <V> FluentFuture<V> addErrorLogging(
            FluentFuture<V> future, Logger logger, String format, Object... args) {
        return addCallback(
                future,
                new dev.enola.common.concurrent.FailureFormatMoreArgumentsLoggingFutureCallback<V>(
                        logger, format, args));
    }

    private static <V, F extends ListenableFuture<V>> F addCallback(
            F future, FutureCallback<V> callback) {
        Futures.addCallback(future, callback, MoreExecutors.directExecutor());
        return future;
    }
}
