/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.infrautils.utils.concurrent;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CancellationException;
import org.slf4j.Logger;

/**
 * Failure logging future callback with a single String message.
 * @author Michael Vorburger.ch
 */
// package-local not public (for the time being)
final class FailureMessageLoggingFutureCallback<V> extends FailureLoggingFutureCallbackBase<V> {
    private final String message;

    FailureMessageLoggingFutureCallback(Logger logger, String message) {
        super(logger);
        this.message = requireNonNull(message, "message is null");
    }

    @Override
    public void onFailure(Throwable throwable) {
        if (throwable instanceof CancellationException) {
            // CancellationException are (typically) no cause for alarm, and debug instead of error level is enough
            // as these can happen during shutdown when we interrupt running threads, and should not pollute logs.
            getLogger().debug("Future (eventually) failed with CancellationException: {}", message, throwable);
        } else {
            getLogger().error("Future (eventually) failed: {}", message, throwable);
        }
    }
}
