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
package dev.enola.common.function;

import com.google.common.collect.Streams;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/** Static utility methods related to {@code Stream} instances. {@link Streams} has more. */
public final class MoreStreams {

    // TODO Use more unchecked instead of checked exceptions in Enola, to reduce the need for
    // this...

    // TODO Move to package dev.enola.common.collect (but must figure out Sneaker relationship)

    // TODO Eventually adopting one of (but which?) real reactive frameworks in Enola overall
    // and rm this may be better? (That would likely be better than doing something such as e.g.
    // https://stackoverflow.com/questions/30117134/aggregate-runtime-exceptions-in-java-8-streams)

    public static <T> CloseableIterable<T> toIterable(Stream<T> stream) {
        // NOT return stream::iterator; because:
        //   (a) This doesn't truly achieve "lazy" transformation in the sense that the Stream's
        //       pipeline  elements (like map, filter) are only executed when the Iterator.next() is
        //       called. This is as lazy as a Stream gets.
        //   (b) The resulting Iterable is single-use. If you try to iterate over it a second time,
        //       you'll get an IllegalStateException because the underlying Stream is closed after
        //       its first consumption.
        return new StreamSingleSupplierCloseableIterable<>(stream);
    }

    // See also dev.enola.common.collect.MoreIterators#SingleIterable
    static final class StreamSingleSupplierCloseableIterable<T> implements CloseableIterable<T> {
        private final AtomicBoolean supplied = new AtomicBoolean(false);
        private final Stream<T> stream;

        private StreamSingleSupplierCloseableIterable(Stream<T> stream) {
            this.stream = stream;
        }

        @Override
        public Iterator<T> iterator() {
            if (!supplied.compareAndSet(false, true))
                throw new IllegalStateException("Value already supplied");
            return stream.iterator();
        }

        @Override
        public void close() {
            stream.close();
        }
    }

    // While waiting for e.g. something like https://bugs.openjdk.org/browse/JDK-8148917
    public static <T, E extends Exception> void forEach(
            Stream<T> stream, CheckedConsumer<T, E> action) throws E {
        if (stream.isParallel()) {
            stream.forEach(Sneaker.sneakyConsumer(action));
        } else {
            forEachInSeq(stream, action);
        }
    }

    /**
     * Returns a stream consisting of the results of applying the given function to the elements of
     * this stream, allowing the mapping function to throw a checked exception.
     *
     * <p>This is an <a
     * href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps">
     * intermediate operation</a>. The {@code mapper} function is not executed, and no exception is
     * thrown, until a <a
     * href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps">
     * terminal operation</a> is invoked on the returned stream.
     *
     * @param stream the stream to map
     * @param mapper a <a
     *     href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Statelessness">
     *     non-interfering, stateless</a> function to apply to each element, which may throw a
     *     checked exception
     * @param <T> The type of the input elements of the stream
     * @param <R> The type of the output elements of the stream
     * @param <E> The type of the checked exception that can be thrown by the mapper
     * @return the new stream
     * @throws E when a terminal operation is executed on the returned stream and the mapper throws
     *     a checked exception. Note that with parallel streams, the exception may (!) be wrapped in
     *     a {@link RuntimeException}.
     */
    public static <T, R, E extends Exception> Stream<R> map(
            Stream<T> stream, CheckedFunction<T, R, E> mapper) throws E {
        return stream.map(Sneaker.sneakyFunction(mapper));
    }

    // This (probably, not verified) loose parallelism, if the stream even has it?
    private static <T, E extends Exception> void forEachInSeq(
            Stream<T> stream, CheckedConsumer<T, E> action) throws E {
        // https://stackoverflow.com/questions/20129762/why-does-streamt-not-implement-iterablet/20130475#20130475
        var iterator = stream.iterator();
        while (iterator.hasNext()) {
            T element = iterator.next();
            action.accept(element);
        }
    }

    private MoreStreams() {}
}
