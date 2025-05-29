/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

public class LoggingExitConsumer implements BiFunction<Integer, Throwable, Void> {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingExitConsumer.class);

    private final String details;

    public LoggingExitConsumer(String details) {
        this.details = details;
    }

    @Override
    public Void apply(Integer integer, Throwable throwable) {
        if (throwable == null) LOG.info("Process {} exited with code {}", details, integer);
        else
            LOG.warn(
                    "Process {} exited with exception {}",
                    details,
                    throwable.getMessage(),
                    throwable);
        return null;
    }
}
