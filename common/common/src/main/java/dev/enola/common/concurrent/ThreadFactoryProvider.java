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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.slf4j.Logger;

import java.util.concurrent.ThreadFactory;

/**
 * Builder for {@link ThreadFactory}. Easier to use than the {@link
 * com.google.common.util.concurrent.ThreadFactoryBuilder}, because it enforces setting all required
 * properties through a staged builder.
 *
 * @author Michael Vorburger.ch, originally for https://www.opendaylight.org
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
