/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.infrautils.utils.concurrent;

import static java.util.Objects.requireNonNull;

import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;

/**
 * Guava Future Callback which logs failures as errors abstract base class.
 *
 * @author Michael Vorburger.ch
 */
// package-local not public (for the time being)
abstract class FailureLoggingFutureCallbackBase<V> implements FutureCallback<V> {

    private final Logger logger;

    FailureLoggingFutureCallbackBase(Logger logger) {
        this.logger = requireNonNull(logger, "logger is null");
    }

    @Override
    public abstract void onFailure(Throwable throwable);

    @Override
    public final void onSuccess(V result) {
        // do nothing
    }

    protected Logger getLogger() {
        return logger;
    }

}
