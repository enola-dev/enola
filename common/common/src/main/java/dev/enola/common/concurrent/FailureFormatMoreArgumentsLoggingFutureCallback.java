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

/**
 * Failure logging future callback with a message format String and 1 argument.
 *
 * @author Michael Vorburger.ch
 */
// package-local not public (for the time being)
final class FailureFormatMoreArgumentsLoggingFutureCallback<V>
        extends FailureLoggingFutureCallbackBase<V> {

    private final String format;
    private final Object[] arguments;

    FailureFormatMoreArgumentsLoggingFutureCallback(
            Logger logger, String format, Object... arguments) {
        super(logger);
        this.format = requireNonNull(format, "format is null");
        // do *NOT* null check these (it's valid)
        this.arguments = arguments;
    }

    @Override
    public void onFailure(Throwable throwable) {
        Object[] argumentsIncludingThrowable = new Object[arguments.length + 1];
        System.arraycopy(arguments, 0, argumentsIncludingThrowable, 0, arguments.length);
        argumentsIncludingThrowable[arguments.length] = throwable;

        getLogger().error("Future (eventually) failed: " + format, argumentsIncludingThrowable);
    }
}
