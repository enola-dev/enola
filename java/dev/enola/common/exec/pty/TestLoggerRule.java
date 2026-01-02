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
import com.github.valfirst.slf4jtest.TestLoggerFactoryResetRule;

import junit.framework.AssertionFailedError;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.event.Level;

public class TestLoggerRule extends TestLoggerFactoryResetRule {

    // TODO Upstream this? See https://github.com/valfirst/slf4j-test/issues/580.

    private final Level captureLevel;
    private final Level printLevel;
    private final TestLogger[] testLoggers;

    public TestLoggerRule(Level captureLevel, Level printLevel, TestLogger... testLoggers) {
        this.captureLevel = captureLevel;
        this.printLevel = printLevel;
        this.testLoggers = testLoggers;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new TestLoggerRuleStatement(base);
    }

    private class TestLoggerRuleStatement extends Statement {
        private final Statement base;

        public TestLoggerRuleStatement(Statement base) {
            super();
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            var testLoggerFactory = TestLoggerFactory.getInstance();
            var originalCaptureLevel = testLoggerFactory.getCaptureLevel();
            var originalPrintLevel = testLoggerFactory.getPrintLevel();
            testLoggerFactory.setCaptureLevel(TestLoggerRule.this.captureLevel);
            testLoggerFactory.setPrintLevel(TestLoggerRule.this.printLevel);
            TestLoggerFactory.clear();
            try {
                base.evaluate();
            } finally {
                TestLoggerFactory.clear();
                testLoggerFactory.setCaptureLevel(originalCaptureLevel);
                testLoggerFactory.setPrintLevel(originalPrintLevel);

                for (var testLogger : TestLoggerRule.this.testLoggers) {
                    var logs = testLogger.getAllLoggingEvents();
                    if (!logs.isEmpty()) throw new AssertionFailedError(logs.toString());
                }
            }
        }
    }
}
