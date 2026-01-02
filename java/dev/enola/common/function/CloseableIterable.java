/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.function;

import static java.util.Collections.emptyIterator;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public interface CloseableIterable<T> extends Iterable<T>, Closeable {

    @Override
    public void close() throws IOException;

    public static <T> CloseableIterable<T> wrap(Iterable<T> iterable, Closeable closeable) {
        return new CloseableIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }

            @Override
            public void close() throws IOException {
                closeable.close();
            }
        };
    }

    public static <T> CloseableIterable<T> empty() {
        return new CloseableIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return emptyIterator();
            }

            @Override
            public void close() {}
        };
    }
}
