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
package dev.enola.common.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configures {@link java.util.logging} (JUL).
 *
 * <p>This is intended to be called once at application startup (main method). It has global side
 * effects, and is not meant to be used from libraries.
 */
public final class JavaUtilLogging {
    private JavaUtilLogging() {}

    /**
     * Configures logging.
     *
     * <p>This sets two {@link System#setProperty(String, String)} properties to work around other
     * logging frameworks, and to configure the format.
     *
     * <p>This removes all existing handlers from the root logger, and adds a single new handler
     * which logs all messages at or above the given {@link Level} to standard error, with colors.
     *
     * @param level the {@link Level} for the root logger and its new handler.
     */
    public static void configure(Level level) {
        // We don't want Spring Boot, which is used in ADK Web, to do configure logging:
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");

        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");

        // Root Logger, so for dev.enola.* but also e.g. ch.vorburger.exec or gRPC, etc.
        var rootLogger = Logger.getLogger("");
        rootLogger.setLevel(level);

        // Remove all existing handlers
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        // Add (only) our fancy colouring handler
        var handler = new LoggingColorConsoleHandler();
        handler.setLevel(level);
        rootLogger.addHandler(handler);

        // =============================
        // Configure specific frameworks
        //   TODO Make these modular and injectable!

        // Disable Java WebSockets logging entirely. The problem is that it, at FINEST, prints
        //   BINARY data, which BREAKS subsequent logging, and that's (very) confusing.
        //   Our own LoggingWebSocketServer already logs what we are interested in.
        Logger.getLogger("org.java_websocket").setLevel(Level.OFF);
    }
}
