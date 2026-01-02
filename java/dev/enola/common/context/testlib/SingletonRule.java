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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/** JUnit <code>@ClassRule</code> (or even just <code>@Rule</code>) for {@link Singleton}. */
public class SingletonRule implements TestRule {

    /** Intended to be statically imported into *Test classes. */
    public static SingletonRule $(Singleton<?>... singletons) {
        return new SingletonRule(singletons, true);
    }

    public static SingletonRule onlyReset(Singleton<?>... singletons) {
        return new SingletonRule(singletons, false);
    }

    private final Singleton<?>[] singletons;
    private boolean doNotReset = false;

    private SingletonRule(Singleton<?>[] singletons, boolean get) {
        if (get)
            for (var singleton : singletons)
                // This may throw IllegalStateException (which is what we want)
                singleton.get();
        this.singletons = singletons;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private <T> Statement statement(Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } finally {
                    if (!doNotReset)
                        for (var singleton : singletons)
                            try {
                                singleton.reset();
                            } catch (IllegalStateException ignored) {
                                // IGNORE!
                            }
                    doNotReset = false;
                }
            }
        };
    }

    public void doNotReset() {
        this.doNotReset = true;
    }
}
