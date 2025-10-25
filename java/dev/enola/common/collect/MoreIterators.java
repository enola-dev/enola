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
package dev.enola.common.collect;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class MoreIterators {

    public static <T, R> Iterator<R> map(Iterator<T> iterator, Function<T, R> mapper) {
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R next() {
                return mapper.apply(iterator.next());
            }
        };
    }

    public static <T> Iterable<T> toIterable(Iterator<T> iterator) {
        return new SingleIterable<T>(iterator);
    }

    // See also dev.enola.common.function.MoreStreams#StreamSingleSupplierCloseableIterable
    private static final class SingleIterable<T> implements Iterable<T> {
        private final AtomicBoolean supplied = new AtomicBoolean(false);
        private final Iterator<T> iterator;

        private SingleIterable(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<T> iterator() {
            if (!supplied.compareAndSet(false, true))
                throw new IllegalStateException("Value already supplied");
            return iterator;
        }
    }

    private MoreIterators() {}
}
