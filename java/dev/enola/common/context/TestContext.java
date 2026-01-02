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
package dev.enola.common.context;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility to detect if it is running under a test, for example to disable some production features.
 *
 * <p>Tests should use <code>TestContextRule</code>.
 */
public final class TestContext {
    private TestContext() {}

    // This is a temporary fallback to support frameworks which cannot use ContextAwareThreadFactory
    // TODO: Remove this when TestContextAwareWebSocketServerTest works without needing this
    @Deprecated public static final AtomicBoolean UNDER_TEST = new AtomicBoolean(false);

    public enum Keys implements Context.Key<Boolean> {
        UNDER_TEST;
    }

    /** Returns true if running under a test, and false if not. */
    public static boolean isUnderTest() {
        return TLC.optional(Keys.UNDER_TEST).orElse(UNDER_TEST.get());
    }
}
