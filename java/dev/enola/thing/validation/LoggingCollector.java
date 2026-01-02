/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.validation;

import dev.enola.thing.Thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingCollector implements Collector2 {

    // TODO Use URILineColumnMessage - if only we had a way to find line numbers...

    private static final Logger LOG = LoggerFactory.getLogger(LoggingCollector.class);

    private boolean hasMessages = false;

    @Override
    public void add(Thing thing, String predicateIRI, String message) {
        hasMessages = true;
        LOG.error(thing.iri() + " -- " + predicateIRI + " : " + message);
    }

    public boolean hasMessages() {
        return hasMessages;
    }
}
