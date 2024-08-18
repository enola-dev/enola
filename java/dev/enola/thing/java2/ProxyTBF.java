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
package dev.enola.thing.java2;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyTBF implements TBF {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderClass, Class<T> thingClass) {
        if (builderClass.equals(ImmutableThing.Builder.class)) {
            return (B) ImmutableThing.builder();
        }

        return (B)
                Proxy.newProxyInstance(
                        builderClass.getClassLoader(),
                        new Class[] {builderClass},
                        new BuilderInvocationHandler(ImmutableThing.builder(), thingClass));
    }

    // TODO Convert class to record?!
    private static class BuilderInvocationHandler implements InvocationHandler {

        private final Thing.Builder<? extends ImmutableThing> immutableThingBuilder;
        private final Class<?> thingClass;

        BuilderInvocationHandler(
                Thing.Builder<? extends ImmutableThing> immutableThingBuilder,
                Class<?> thingClass) {
            this.immutableThingBuilder = immutableThingBuilder;
            this.thingClass = thingClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) return InvocationHandler.invokeDefault(proxy, method, args);
            if (!method.getName().equals("build"))
                return method.invoke(immutableThingBuilder, args);
            return Proxy.newProxyInstance(
                    thingClass.getClassLoader(),
                    new Class[] {thingClass},
                    new ThingInvocationHandler(immutableThingBuilder.build()));
        }

        // TODO toString() ?
    }

    // TODO Convert class to record?!
    private static class ThingInvocationHandler implements InvocationHandler {

        private final ImmutableThing immutableThingBuilder;

        ThingInvocationHandler(ImmutableThing immutableThingBuilder) {
            this.immutableThingBuilder = immutableThingBuilder;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) return InvocationHandler.invokeDefault(proxy, method, args);
            return method.invoke(immutableThingBuilder, args);
        }

        // TODO toString() ?
    }
}
