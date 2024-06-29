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

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.Nullable;

/**
 * Contexts 🧿 put things into perspective!
 *
 * <p>This class is NOT thread safe. Might you want to use {@link TLC} instead?
 *
 * @author <a href="http://www.vorburger.ch">Michael Vorburger.ch</a>
 */
public class Context implements AutoCloseable {

    private final @Nullable Context parent;

    private @Nullable Entry last = null;
    private boolean closed = false;

    public Context(Context parent) {
        this.parent = requireNonNull(parent);
    }

    public Context() {
        this.parent = null;
    }

    /**
     * Push, but not too hard…
     *
     * @param key Key, which must implement {@link #equals(Object)} correctly, and should have a
     *     useful {@link #toString()}} implementation; in practice, it often IS actually simply a
     *     {@link String} (but it technically does not necessarily have to be).
     * @param value Value to associate with the key.
     * @return this, for chaining.
     */
    public Context push(Object key, Object value) {
        check();
        last = new Entry(key, value, last);
        return this;
    }

    /** Get the value for the given key from this or its parent context. */
    public @Nullable Object get(Object key) {
        check();
        var current = last;
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.previous;
        }
        if (parent != null) return parent.get(key);
        else return null;
    }

    // Nota bene: This (kind of) Stack-like data structure (intentionally)
    // does not have (need) any pop() ("goes the weasel”) kind of method!

    /** Close this context. Don't use it anymore! */
    @Override
    public void close() {
        closed = true;
        // Free up memory!
        last = null;
        TLC.reset(parent);
    }

    private void check() {
        if (closed) {
            throw new IllegalStateException("Context already closed");
        }
    }

    private static class Entry {
        final Object key;
        final Object value;
        final @Nullable Entry previous;

        Entry(Object key, Object value, @Nullable Entry previous) {
            this.key = key;
            this.value = value;
            this.previous = previous;
        }
    }
}
