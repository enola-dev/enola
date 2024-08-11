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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Contexts 🧿 put things into perspective!
 *
 * <p>Contexts are "hierarchical", and child contexts "mask" keys in their parent.
 *
 * <p>This class is NOT thread safe. Might you want to use {@link TLC} instead?
 *
 * @author <a href="http://www.vorburger.ch">Michael Vorburger.ch</a>
 */
public class Context implements AutoCloseable {

    public interface Key<T> {}

    private static final Logger LOG = LoggerFactory.getLogger(Context.class);

    private final @Nullable Context parent;

    @Nullable Entry last = null;
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
     * @param key Key.
     * @param value Value to associate with the key.
     * @return this, for chaining.
     */
    public <K extends Enum<K> & Key<T>, T> Context push(K key, T value) {
        return _push(key, value);
    }

    public <T> Context push(Class<T> key, T value) {
        _push(key, value); // NOT .getName()
        return this;
    }

    private Context _push(Object key, Object value) {
        check();
        last = new Entry(key, value, last);
        return this;
    }

    /** Get the value for the given key, from this or its parent context. May be null. */
    public <K extends Enum<K> & Key<T>, T> @Nullable T get(K key) {
        return (T) _get(key);
    }

    /**
     * Gets the instance of Class, from this or its parent context. Never null, may throw
     * IllegalStateException.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> key) {
        if (isEmpty()) throw new IllegalStateException("Context is empty, no: " + key);
        var object = _get(key);
        if (object == null)
            throw new IllegalStateException("Context has no " + key + "; only:\n" + toString("  "));
        if (key.isInstance(object)) return (T) object;
        throw new IllegalStateException("Context's " + key + " is a " + object.getClass());
    }

    public <T> Optional<T> optional(Class<T> key) {
        return Optional.ofNullable((T) _get(key));
    }

    private @Nullable Object _get(Object key) {
        check();
        var current = last;
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.previous;
        }
        if (parent != null) return parent._get(key);
        else return null;
    }

    private boolean isEmpty() {
        return last == null && parent == null;
    }

    // Nota bene: This (kind of) Stack-like data structure (intentionally)
    // does not have (need) any pop() ("goes the weasel”) kind of method!

    void append(Appendable a, String indent) {
        try {
            var current = last;
            while (current != null) {
                a.append(indent);
                a.append(current.key.toString());
                a.append(" => ");
                a.append(current.value.toString());
                a.append('\n');
                current = current.previous;
            }
            if (parent != null) parent.append(a, indent + ContextualizedException.INDENT);
        } catch (IOException e) {
            LOG.error("append() hit an IOException", e);
        }
    }

    private String toString(String indent) {
        var sb = new StringBuilder();
        append(sb, indent);
        return sb.toString();
    }

    public String toString() {
        return toString("");
    }

    /** Close this context. Don't use it anymore! */
    @Override
    public void close() {
        closed = true;
        TLC.reset(parent);

        // NB: It's tempting to do "last = null" here, intending to free up memory;
        // but doing so breaks e.g. the ContextsTest#exceptionWithContext(). We do
        // NOT have to do it to free memory, because Context (should) will be GC.
    }

    private void check() {
        if (closed) {
            throw new IllegalStateException("Context already closed");
        }
    }

    private record Entry(Object key, Object value, @Nullable Entry previous) {
        private Entry(Object key, Object value, @Nullable Entry previous) {
            this.key = key;
            this.value = value;
            this.previous = previous;
        }
    }
}
