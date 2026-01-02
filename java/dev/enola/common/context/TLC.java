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
package dev.enola.common.context;

import com.google.errorprone.annotations.ThreadSafe;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * TLC is the Thread Local {@link Context}. (Also known as "Tender Loving Care".)
 *
 * <p>This is useful to hold values that are user- or request-dependent.
 *
 * <p>For things which are "global", just use a {@link Singleton} instead.
 *
 * @author <a href="http://www.vorburger.ch">Michael Vorburger.ch</a>
 */
@ThreadSafe
public final class TLC {

    // TODO Use Java 21+ e JEP 446 --preview ScopedValue instead of ThreadLocal
    // https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ScopedValue.html

    private static final ThreadLocal<Context> threadLocalContext = new ThreadLocal<>();

    /**
     * Opens a new (!) {@link Context}, "stacked" over the current one (if any).
     *
     * <p>This is typically invoked from a try-with-resources, as the returned context must be
     * closed again at some point; so the typical usage is: <code>try (var ctx = TLC.open()) {
     * </code>.
     */
    public static Context open() {
        Context next;
        var previous = threadLocalContext.get();
        if (previous != null) {
            next = new Context(previous);
        } else {
            next = new Context();
        }
        setThreadLocalContext(next);
        return next;
    }

    /* package local! */ static void setThreadLocalContext(Context context) {
        threadLocalContext.set(context);
    }

    /**
     * See {@link dev.enola.common.context.Context#get(Class)}.
     *
     * <p>Use {@link #optional(Enum)} to check if key is available in Context.
     */
    public static <K extends Enum<K> & Context.Key<T>, T> T get(K key) {
        return context(key).get(key);
    }

    /** See {@link Context#optional(Enum)}. */
    public static <K extends Enum<K> & Context.Key<T>, T> Optional<T> optional(K key) {
        var tlc = threadLocalContext.get();
        if (tlc == null) return Optional.empty();
        return tlc.optional(key);
    }

    /** See {@link Context#get(java.lang.Class)}. */
    public static <T> T get(Class<T> klass) {
        return context(klass).get(klass);
    }

    public static <T> Optional<T> optional(Class<T> klass) {
        var tlc = threadLocalContext.get();
        if (tlc == null) return Optional.empty();
        return tlc.optional(klass);
    }

    private static Context context(Object debug) {
        var tlc = threadLocalContext.get();
        if (tlc == null) {
            throw new IllegalStateException(
                    "Missing TLC.open() in call chain, can't get: " + debug);
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
