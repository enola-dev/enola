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
package dev.enola.cli.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Application {

    int loggingVerbosity;
    private boolean loggingIsConfigured = false;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    void loggingIsConfigured() {
        loggingIsConfigured = true;
    }

    protected Logger log() {
        if (!loggingIsConfigured) throw new IllegalStateException();
        return logger;
    }

    protected abstract void start();
}
