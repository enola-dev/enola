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

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingDemo {
    public static void main(String[] args) {
        JavaUtilLogging.configure(Level.ALL);
        demoLogging();
    }

    public static void demoLogging() {
        java.util.logging.Logger julLogger = Logger.getLogger(LoggingDemo.class.getName());
        org.slf4j.Logger slf4jLogger = org.slf4j.LoggerFactory.getLogger(LoggingDemo.class);
        org.apache.commons.logging.Log jclLogger =
                org.apache.commons.logging.LogFactory.getLog(LoggingDemo.class);

        // Visible with -v
        jclLogger.error("JCL ERROR Logging Test");
        slf4jLogger.error("SLF ERROR Logging Test");
        julLogger.severe("JUL SEVERE Logging Test");

        // Visible with -vv
        jclLogger.warn("JCL WARN Logging Test");
        slf4jLogger.warn("SLF WARN Logging Test");
        julLogger.warning("JUL WARNING Logging Test");

        // Visible with -vvv
        jclLogger.info("JCL INFO Logging Test");
        slf4jLogger.info("SLF INFO Logging Test");
        julLogger.info("JUL INFO Logging Test");
        julLogger.config("JUL CONFIG Logging Test");

        // Visible with -vvvv
        jclLogger.debug("JCL DEBUG Logging Test");
        slf4jLogger.debug("SLF DEBUG Logging Test");
        julLogger.fine("JUL FINE Logging Test");
        julLogger.finer("JUL FINER Logging Test");

        // Visible with -vvvvv
        jclLogger.trace("JCL TRACE Logging Test");
        slf4jLogger.trace("SLF TRACE Logging Test");
        julLogger.finest("JUL FINEST Logging Test");
    }
}
