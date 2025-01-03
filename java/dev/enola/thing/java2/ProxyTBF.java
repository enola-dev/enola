/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import com.google.common.reflect.Reflection;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.IImmutableThing;
import dev.enola.thing.impl.ImmutableThing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ProxyTBF implements TBF {

    private final TBF wrap;

    /**
     * Constructor.
     *
     * @param wrap is a {@link TBF} such as {@link ImmutableThing#FACTORY} or {@link
     *     dev.enola.thing.impl.MutableThing#FACTORY}.
     */
    public ProxyTBF(TBF wrap) {
        this.wrap = wrap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<?>> B create(
            Class<B> builderClass, Class<T> thingClass) {
        if (builderClass.equals(Thing.Builder.class)) {
            return (B) wrap.create();
        }

        var wrapped = // an ImmutableThing.Builder or a MutableThing (but NOT another Proxy)
                (Thing.Builder<? extends IImmutableThing>) wrap.create();
        var handler = new BuilderInvocationHandler(wrapped, thingClass);
        // return (B) Proxy.newProxyInstance(builderClass.getClassLoader(),
        //     new Class[] {builderClass}, handler);
        var proxy = Reflection.newProxy(builderClass, handler);
        if (!(builderClass.isInstance(proxy))) throw new IllegalArgumentException(proxy.toString());
        return proxy;
    }

    // TODO Consider using Guava's AbstractInvocationHandler?

    private record BuilderInvocationHandler(
            Thing.Builder<? extends IImmutableThing> immutableThingBuilder, Class<?> thingClass)
            implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) return InvocationHandler.invokeDefault(proxy, method, args);
            if (!method.getName().equals("build"))
                try {
                    return method.invoke(immutableThingBuilder, args);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "proxy="
                                    + proxy
                                    + "; method="
                                    + method
                                    + "; args="
                                    + Arrays.toString(args),
                            e);
                }
            var handler = new ThingInvocationHandler(immutableThingBuilder.build());
            // return Proxy.newProxyInstance(thingClass.getClassLoader(), new Class[] {thingClass},
            // handler);
            return Reflection.newProxy(thingClass, handler);
        }
    }

    private record ThingInvocationHandler(IImmutableThing immutableThingBuilder)
            implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) return InvocationHandler.invokeDefault(proxy, method, args);
            return method.invoke(immutableThingBuilder, args);
        }
    }
}
