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
package dev.enola.ai.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.function.Consumer;

class Slf4jLoggingConsumer implements Consumer<LoggingMessageNotification> {

    // TODO: If https://github.com/modelcontextprotocol/java-sdk/pull/503
    //   is accepted and merged, the eventually replace this by that?
    //   But that doesn't have the origin... hm. Add it?

    private final String origin;

    public Slf4jLoggingConsumer(String origin) {
        this.origin = origin;
    }

    @Override
    public void accept(LoggingMessageNotification notif) {
        Logger log = LoggerFactory.getLogger(notif.logger());
        log.atLevel(convert(notif.level()))
                .log("{} : {} : {}", origin, notif.logger(), notif.data());
    }

    private Level convert(McpSchema.LoggingLevel level) {
        return switch (level) {
            case DEBUG -> Level.DEBUG;
            case INFO, NOTICE -> Level.INFO;
            case WARNING -> Level.WARN;
            case ERROR, CRITICAL, ALERT, EMERGENCY -> Level.ERROR;
        };
    }
}
