/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.cli;

import dev.enola.common.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Lifecycle {

    private static final Logger LOG = LoggerFactory.getLogger(Lifecycle.class);

    static void start() {
        LOG.info(
                "Hi! \uD83D\uDC4B I'm https://Enola.dev {}. "
                        + "\uD83D\uDC7D Resistance \uD83D\uDC7E is futile. We are ONE. "
                        + "What's your goal, today?\n",
                Version.get());
    }

    private Lifecycle() {}
}
