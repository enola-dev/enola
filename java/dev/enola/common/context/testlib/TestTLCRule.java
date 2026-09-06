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

import com.google.common.collect.ImmutableMap;

import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestTLCRule
        implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(TestTLCRule.class);

    // TODO Use https://github.com/google/guava/wiki/NewCollectionTypesExplained#classtoinstancemap

    public static <T> TestTLCRule of(Class<T> klass, T instance) {
        return new TestTLCRule(ImmutableMap.of(klass, instance));
    }

    public static <T> TestTLCRule of(Context.Key<T> klass, T instance) {
        return new TestTLCRule(ImmutableMap.of(klass, instance), true);
    }

    private final ImmutableMap<Class<?>, ?> pushedClasses;
    private final ImmutableMap<Context.Key<?>, ?> pushedKeys;

    // TODO private; callers should should static of() method - just because it's shorter to write
    public TestTLCRule(ImmutableMap<Class<?>, ?> pushes) {
        this.pushedClasses = pushes;
        this.pushedKeys = ImmutableMap.of();
    }

    protected TestTLCRule(ImmutableMap<Context.Key<?>, ?> pushes, boolean ignore) {
        this.pushedClasses = ImmutableMap.of();
        this.pushedKeys = pushes;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        start(context);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        end(context);
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        start(context);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        end(context);
    }

    @SuppressWarnings("unchecked")
    private <T> void start(ExtensionContext context) {
        before();
        var ctx = TLC.open();
        for (var push : pushedClasses.entrySet()) {
            Class<T> clazz = (Class<T>) push.getKey();
            T instance = (T) push.getValue();
            ctx.push(clazz, instance);
        }
        for (var push : pushedKeys.entrySet()) {
            Context.Key<T> key = (Context.Key<T>) push.getKey();
            T instance = (T) push.getValue();
            ctx.push(key, instance);
        }
        context.getStore(NAMESPACE).put(context.getUniqueId(), ctx);
    }

    private void end(ExtensionContext context) {
        try {
            var ctx = (Context) context.getStore(NAMESPACE).remove(context.getUniqueId());
            if (ctx != null) {
                ctx.close();
            }
        } finally {
            after();
        }
    }

    protected void before() {}

    protected void after() {}
}
