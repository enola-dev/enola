/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.infrautils.utils.concurrent;

import static java.util.Objects.requireNonNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;

/**
 * Failure logging future callback with a message format String and 1 argument.
 * @author Michael Vorburger.ch
 */
// package-local not public (for the time being)
final class FailureFormat1ArgumentLoggingFutureCallback<V> extends FailureLoggingFutureCallbackBase<V> {

    private final String format;
    private final Object arg;

    FailureFormat1ArgumentLoggingFutureCallback(Logger logger, String format, Object arg) {
        super(logger);
        this.format = "Future (eventually) failed: " + requireNonNull(format, "format is null");
        // do *NOT* null check this one (that's valid)
        this.arg = arg;
    }

    @Override
    @SuppressFBWarnings("SLF4J_FORMAT_SHOULD_BE_CONST")
    public void onFailure(Throwable throwable) {
        getLogger().error(format, arg, throwable);
    }

}
