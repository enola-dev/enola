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
package dev.enola.common.secret;

import dev.enola.common.function.Processor;

import org.jspecify.annotations.Nullable;

import java.io.Console;
import java.lang.ref.Cleaner;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Secret 🔑 is a wrapper around an auto-cleaned char[]. It's intended to hold sensitive character
 * data like passwords, pass phrases, access tokens like API keys and similar credentials, often
 * related to configuration. It is cleared from memory when the Secret is {@link #close()}d.
 *
 * @author <a href="https://www.vorburger.ch">Michael Vorburger.ch</a>, pair programmed together <a
 *     href="https://gemini.google.com/app/58da21bb8360498c">with Google Gemini</a> on 2025-05-11.
 */
public final class Secret implements AutoCloseable, Processor<char[]> {

    // TODO Factor out a class SecureData<T> and make Secret extends SecureData<char[]>
    //   Then also have another class SecretBytes extends SecureData<byte[]> e.g. for private keys.

    private static final Cleaner cleaner = Cleaner.create();
    private final AtomicBoolean isCleared = new AtomicBoolean(false);
    private final Cleaner.Cleanable cleanable;
    private @Nullable char @Nullable [] data;

    // dataToClean is a reference to (not a copy of) Secret's data
    private record State(char[] dataToClean) implements Runnable {
        @Override
        public void run() {
            Arrays.fill(dataToClean, '\0');
        }
    }

    /**
     * Creates a Secret instance from a character array. The provided array is copied internally.
     * The input array argument is then zeroed out immediately.
     *
     * @param data The sensitive character data.
     */
    public Secret(char[] data) {
        // Copy to prevent external modification
        this.data = Arrays.copyOf(data, data.length);

        this.cleanable = cleaner.register(this, new State(this.data));

        // Zero out the input / argument (NOT the field)
        Arrays.fill(data, '\0');
    }

    /**
     * Deprecated constructor. Prefer using {@link #Secret(char[])} instead, if you can somehow
     * originally work with a char array (think e.g. {@link Console#readPassword()}) already, to
     * avoid using String all together.
     *
     * @param data String
     */
    @Deprecated
    public Secret(String data) {
        this(data.toCharArray());
    }

    /**
     * Safely processes the sensitive data using a consumer. A copy of the internal char array
     * containing the secret data is passed to the consumer for immediate use. Its content is
     * cleared again immediately after. This is the preferred and secure way to use the secret data,
     * as it prevents creating long-lived copies or String objects outside the controlled scope.
     *
     * @param consumer The consumer to process the char array.
     * @throws IllegalStateException if the Secret instance has already been {@link #close()}}.
     */
    @Override
    public void process(Consumer<char[]> consumer) {
        if (isCleared.get()) throw new IllegalStateException("Secret has been closed and cleared.");
        var dataCopy = Arrays.copyOf(this.data, this.data.length);
        try {
            consumer.accept(dataCopy);
        } finally {
            Arrays.fill(dataCopy, '\0');
        }
    }

    /**
     * Safely processes the sensitive data using a function. A copy of the internal char array
     * containing the secret data is passed to the function for immediate use. Its content is
     * cleared again immediately after. This is the preferred and secure way to use the secret data,
     * as it prevents creating long-lived copies or String objects outside the controlled scope.
     *
     * <p>Do not copy the secret data within the mapping if you can avoid it! When you really must,
     * e.g. because you need to pass it to an existing less secure API which does not offer a
     * (functional) "on-demand" alternative, then use <code>secret.map(String::new)</code>.
     *
     * @param mapping The function to process the char array.
     * @throws IllegalStateException if the Secret instance has already been {@link #close()}}.
     */
    @Override
    public <R> R map(Function<char[], R> mapping) {
        if (isCleared.get()) throw new IllegalStateException("Secret has been closed and cleared.");
        var dataCopy = Arrays.copyOf(this.data, this.data.length);
        try {
            return mapping.apply(dataCopy);
        } finally {
            Arrays.fill(dataCopy, '\0');
        }
    }

    /**
     * Clears the sensitive data by filling the internal char array with zeros. This method is
     * automatically called when using try-with-resources. It is idempotent; calling it multiple
     * times has no additional effect after the first call. The {@link #process(Consumer)} and
     * {@link #map(Function)} methods will throw an IllegalStateException if invoked after this
     * method.
     */
    @Override
    public void close() {
        if (isCleared.compareAndSet(false, true)) {
            this.cleanable.clean();
            this.data = null;
        }
    }
}
