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

import com.google.errorprone.annotations.ThreadSafe;

import org.jspecify.annotations.Nullable;

/**
 * TLC is the Thread Local {@link Context}. (Also known as "Tender Loving Care".)
 *
 * @author <a href="http://www.vorburger.ch">Michael Vorburger.ch</a>
 */
@ThreadSafe
public final class TLC {

    // TODO Offer more type-safe signatures, with (Key<T> key, T value)

    private static final ThreadLocal<Context> threadLocalContext = new ThreadLocal<>();

    /**
     * Opens a new (!) {@link Context}, "stacked" over the current one (if any).
     *
     * <p>This is typically invoked from a try-with-resources, as the returned context must be
     * closed again at some point; so the typical usage is: <tt>try (var ctx = TLC.open()) {</tt>.
     */
    public static Context open() {
        Context next;
        var previous = threadLocalContext.get();
        if (previous != null) {
            next = new Context(previous);
        } else {
            next = new Context();
        }
        threadLocalContext.set(next);
        return next;
    }

    /** See {@link dev.enola.common.context.Context#get(java.lang.Object). */
    public static @Nullable Object get(Object key) {
        return context().get(key);
    }

    /** See {@link dev.enola.common.context.Context#get(java.lang.Class). */
    public static <T> T get(Class<T> klass) {
        return context().get(klass);
    }

    private static Context context() {
        var tlc = threadLocalContext.get();
        if (tlc == null) {
            throw new IllegalStateException("Missing TLC.open() in call chain!");
        }
        return tlc;
    }

    /* package-local, always keep; never make public! */
    static void reset(@Nullable Context context) {
        threadLocalContext.set(context);
    }

    /* package-local, always keep; never make public! */
    static @Nullable Context get() {
        return threadLocalContext.get();
    }

    private TLC() {}
}
