/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import dev.enola.common.context.TLC;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestTLCRule implements TestRule {

    // TODO Use https://github.com/google/guava/wiki/NewCollectionTypesExplained#classtoinstancemap

    public static <T> TestTLCRule of(Class<T> klass, T instance) {
        return new TestTLCRule(ImmutableMap.of(klass, instance));
    }

    private final ImmutableMap<Class<?>, ?> pushes;

    // TODO turn into static of() method - just because it's shorter to write
    public TestTLCRule(ImmutableMap<Class<?>, ?> pushes) {
        this.pushes = pushes;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    @SuppressWarnings("unchecked")
    private <T> Statement statement(Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (var ctx = TLC.open()) {
                    for (var push : pushes.entrySet()) {
                        Class<T> clazz = (Class<T>) push.getKey();
                        T instance = (T) push.getValue();
                        ctx.push(clazz, instance);
                    }
                    base.evaluate();
                }
            }
        };
    }
}
