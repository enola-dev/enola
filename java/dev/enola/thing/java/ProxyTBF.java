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
package dev.enola.thing.java;

import com.google.common.reflect.Reflection;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.IImmutableThing;
import dev.enola.thing.impl.ImmutableThing;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class ProxyTBF implements TBF {

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
    public boolean handles(Class<?> builderInterface) {
        // Skip Proxy if wrapped delegate can handle it
        // This makes TBFChain more efficient, because e.g.
        // Thing.Builder.class can be "passed through".
        return !wrap.handles(builderInterface);
    }

    @Override
    public boolean handles(String typeIRI) {
        // ProxyTBF handles any typeIRI, currently.
        // TODO Make it based on whether create() implementation uses default class or not.
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Thing.Builder<Thing> create(String typeIRI) {
        var classPair = TypeToBuilder.typeToBuilder(typeIRI);
        return create(classPair.builderClass(), classPair.thingClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface) {
        return (B)
                create(
                        wrap,
                        -1,
                        (Class<Thing.Builder<IImmutableThing>>) builderInterface,
                        (Class<IImmutableThing>) thingInterface);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface, int expectedSize) {
        return (B)
                create(
                        wrap,
                        expectedSize,
                        (Class<Thing.Builder<IImmutableThing>>) builderInterface,
                        (Class<IImmutableThing>) thingInterface);
    }

    @SuppressWarnings("unchecked")
    private static Thing.Builder<? extends IImmutableThing> create(
            TBF tbf,
            int expectedSize,
            Class<Thing.Builder<IImmutableThing>> builderInterface,
            Class<IImmutableThing> thingInterface) {
        if (builderInterface.equals(Thing.Builder.class))
            return createX(tbf, builderInterface, thingInterface, expectedSize);

        var wrappedBuilder = // an ImmutableThing.Builder or a MutableThing (but NOT another Proxy)
                (Thing.Builder<? extends IImmutableThing>)
                        createX(tbf, builderInterface, thingInterface, expectedSize);
        var handler =
                new BuilderInvocationHandler(tbf, wrappedBuilder, builderInterface, thingInterface);
        var proxy = Reflection.newProxy(builderInterface, handler);
        if (!(builderInterface.isInstance(proxy)))
            throw new IllegalArgumentException(proxy.toString());
        return proxy;
    }

    private static Thing.Builder<? extends IImmutableThing> createX(
            TBF tbf,
            Class<Thing.Builder<IImmutableThing>> builderInterface,
            Class<IImmutableThing> thingInterface,
            int expectedSize) {
        if (expectedSize == -1) return tbf.create(builderInterface, thingInterface);
        else return tbf.create(builderInterface, thingInterface, expectedSize);
    }

    // TODO Consider using Guava's AbstractInvocationHandler?

    private record BuilderInvocationHandler(
            TBF tbf,
            Thing.Builder<? extends IImmutableThing> immutableThingBuilder,
            Class<Thing.Builder<IImmutableThing>> builderInterface,
            Class<IImmutableThing> thingInterface)
            implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                throws Throwable {
            if (method.isDefault()) return InvocationHandler.invokeDefault(proxy, method, args);
            if (method.getName() == "iri" && args != null && args.length > 0) {
                immutableThingBuilder.iri((String) args[0]);
                return proxy;
            } else if (method.getName().equals("build")) {
                var built = immutableThingBuilder.build();
                var handler =
                        new ThingInvocationHandler(tbf, built, builderInterface, thingInterface);
                return Reflection.newProxy(thingInterface, handler);
            } else if (method.getName().equals("toString")) return toString();
            // else
            try {
                return method.invoke(immutableThingBuilder, args);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Interface may be missing default method implementations?!"
                                + " immutableThingBuilder="
                                + immutableThingBuilder
                                + "; proxy="
                                + proxy
                                + "; method="
                                + method
                                + "; args="
                                + Arrays.toString(args),
                        e);
            }
        }

        @Override
        public String toString() {
            return "ProxyTBF.BuilderInvocationHandler{builderInterface="
                    + builderInterface
                    + ", builder="
                    + immutableThingBuilder
                    + "}";
        }
    }

    private record ThingInvocationHandler(
            TBF tbf,
            IImmutableThing thing,
            Class<Thing.Builder<IImmutableThing>> builderInterface,
            Class<IImmutableThing> thingInterface)
            implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                throws Throwable {
            if (method.isDefault()) return InvocationHandler.invokeDefault(proxy, method, args);
            if (method.getName().equals("copy")) return copy();
            if (method.getName().equals("toString")) return toString();
            return method.invoke(thing, args);
        }

        @SuppressWarnings(
                "Immutable") // TODO Remove when switching to (TBD) PredicatesObjects.Visitor
        private Thing.Builder<? extends IImmutableThing> copy() {
            var size = thing.properties().size();
            var builder = create(tbf, size, builderInterface, thingInterface);
            builder.iri(thing.iri());
            thing.properties()
                    .forEach(
                            (predicateIRI, object) -> {
                                var datatypeIRI = thing.datatype(predicateIRI);
                                builder.set(predicateIRI, object, datatypeIRI);
                            });
            return builder;
        }

        @Override
        public String toString() {
            return "ProxyTBF.ThingInvocationHandler{thingInterface="
                    + thingInterface
                    + ", thing="
                    + thing
                    + "}";
        }
    }
}
