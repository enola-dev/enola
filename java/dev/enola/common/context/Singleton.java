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
package dev.enola.common.context;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Singleton.
 *
 * <p>Singletons statically hold a value for the lifetime of the JVM.
 *
 * <p>This is great for "global" services; some initialization code called at the start of an
 * application sets its value, which can then be easily looked up from anywhere in an application.
 * It is not possible to later change or reset that value - this is an intentional design decision.
 *
 * <p>For things which are e.g. user- or request-dependent, just use {@link TLC} instead.
 *
 * <p>In tests, use this together with the JUnit SingletonRule. See also SingleTest for how to use
 * this; TL;DR is: <code>class MyService {
 *   public static final Singleton<MyService> SINGLETON = new Singleton<>() {};</code>. For
 * configurable services used in several tests, it's convenient to define something like: <code>
 * public static Singleton<MyService> set(...) { return SINGLETON.set(new MyService(...));</code>
 *
 * @author <a href="http://www.vorburger.ch">Michael Vorburger.ch</a>
 */
public abstract class Singleton<T> implements Supplier<T> {

    private @Nullable T value;

    @CanIgnoreReturnValue
    public Singleton<T> set(T value) {
        if (this.value != null) throw new IllegalStateException();
        if (value == null) throw new IllegalArgumentException("Use reset() instead of set(null)");
        this.value = value;
        return this;
    }

    @Override
    public T get() {
        if (value == null) throw new IllegalStateException();
        else return value;
    }

    // TODO Make Move SingletonRule to this package, and make this package private!
    public void reset() {
        if (value == null) throw new IllegalStateException();
        value = null;
    }
}
