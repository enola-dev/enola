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
package dev.enola.common.context.testlib;

import dev.enola.common.context.Singleton;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

/** JUnit Jupiter Extension for {@link Singleton}. */
public class SingletonRule
        implements BeforeAllCallback,
                BeforeEachCallback,
                AfterEachCallback,
                AfterAllCallback,
                TestWatcher {

    /** Intended to be statically imported into *Test classes. */
    public static SingletonRule $(Singleton<?>... singletons) {
        return new SingletonRule(singletons, true);
    }

    public static SingletonRule onlyReset(Singleton<?>... singletons) {
        return new SingletonRule(singletons, false);
    }

    private final Singleton<?>[] singletons;
    private final boolean get;
    private boolean doNotReset = false;
    private boolean isClassLevel = false;

    private SingletonRule(Singleton<?>[] singletons, boolean get) {
        this.get = get;
        if (get)
            for (var singleton : singletons)
                // This may throw IllegalStateException (which is what we want)
                singleton.get();
        this.singletons = singletons;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        this.isClassLevel = true;
        if (get) for (var singleton : singletons) singleton.get();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        if (get) for (var singleton : singletons) singleton.get();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (!isClassLevel) {
            reset();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        reset();
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        if (!isClassLevel) {
            reset();
        }
    }

    private void reset() {
        if (!doNotReset)
            for (var singleton : singletons)
                try {
                    singleton.reset();
                } catch (IllegalStateException ignored) {
                    // IGNORE!
                }
        doNotReset = false;
    }

    public void doNotReset() {
        this.doNotReset = true;
    }
}
