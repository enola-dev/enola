/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.rx;

import static org.slf4j.LoggerFactory.getLogger;

import static java.util.Objects.requireNonNull;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

import org.slf4j.Logger;

public final class RxJavaPluginsSetup {

    private static final Logger LOG = getLogger(RxJavaPluginsSetup.class);

    private static final Consumer<? super Throwable> ERROR_HANDLER =
            throwable -> {
                LOG.warn("Uncaught RxJava exception: {}", throwable.getMessage(), throwable);
            };

    public static void setUp() {
        errorHandler();

        // TODO Dev Mode global flag... see how Quarkus & Spring do it?
        // TODO hu.akarnokd.rxjava3:rxjava3-extensions:3.x.x RxJavaAssemblyTracking.enable();
        // TODO RxDogTag ?
    }

    private static void errorHandler() {
        var errorHandler = RxJavaPlugins.getErrorHandler();
        if (errorHandler != null && errorHandler != ERROR_HANDLER) {
            LOG.error("RxJavaErrorHandler already set: {}", errorHandler);
        } else RxJavaPlugins.setErrorHandler(requireNonNull(ERROR_HANDLER));
    }

    private RxJavaPluginsSetup() {}
}
