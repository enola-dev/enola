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
package dev.enola.common.context.testlib;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.context.TestContext;

/**
 * JUnit rule to set {@link TestContext}.
 *
 * <p>Usage: <code>@Rule public TestContextRule rule = new TestContextRule();</code>
 *
 * @see TestContext
 */
public class TestContextRule extends TestTLCRule {

    public TestContextRule() {
        super(ImmutableMap.of(TestContext.Keys.UNDER_TEST, true), true);
    }

    @Override
    protected void before() {
        super.before();
        TestContext.UNDER_TEST.set(true);
    }

    @Override
    protected void after() {
        super.after();
        TestContext.UNDER_TEST.set(false);
    }
}
