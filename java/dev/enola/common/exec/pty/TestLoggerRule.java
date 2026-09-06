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
package dev.enola.common.exec.pty;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.AssertionFailedError;
import org.slf4j.event.Level;

public class TestLoggerRule implements BeforeEachCallback, AfterEachCallback {

    // TODO Upstream this? See https://github.com/valfirst/slf4j-test/issues/580.

    private final Level captureLevel;
    private final Level printLevel;
    private final TestLogger[] testLoggers;
    private Level originalCaptureLevel;
    private Level originalPrintLevel;

    public TestLoggerRule(Level captureLevel, Level printLevel, TestLogger... testLoggers) {
        this.captureLevel = captureLevel;
        this.printLevel = printLevel;
        this.testLoggers = testLoggers;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        var testLoggerFactory = TestLoggerFactory.getInstance();
        originalCaptureLevel = testLoggerFactory.getCaptureLevel();
        originalPrintLevel = testLoggerFactory.getPrintLevel();
        testLoggerFactory.setCaptureLevel(captureLevel);
        testLoggerFactory.setPrintLevel(printLevel);
        TestLoggerFactory.clear();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        var testLoggerFactory = TestLoggerFactory.getInstance();
        TestLoggerFactory.clear();
        if (originalCaptureLevel != null) testLoggerFactory.setCaptureLevel(originalCaptureLevel);
        if (originalPrintLevel != null) testLoggerFactory.setPrintLevel(originalPrintLevel);

        for (var testLogger : testLoggers) {
            var logs = testLogger.getAllLoggingEvents();
            if (!logs.isEmpty()) throw new AssertionFailedError(logs.toString());
        }
    }
}
