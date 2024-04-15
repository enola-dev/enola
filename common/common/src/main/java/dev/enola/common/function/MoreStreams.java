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
package dev.enola.common.function;

import com.google.common.collect.Streams;

import java.util.stream.Stream;

/** Static utility methods related to {@code Stream} instances. {@link Streams} has more. */
public final class MoreStreams {

    // TODO Eventually adopting one of (but which?) real reactive frameworks in Enola overall
    // and rm this may be better? (That would likely be better than doing something such as e.g.
    // https://stackoverflow.com/questions/30117134/aggregate-runtime-exceptions-in-java-8-streams)

    // While waiting for e.g. something like https://bugs.openjdk.org/browse/JDK-8148917
    public static <T, E extends Exception> void forEach(
            Stream<T> stream, CheckedConsumer<T, E> action) throws E {
        if (stream.isParallel()) {
            sneakyForEach(stream, action);
        } else {
            forEachInSeq(stream, action);
        }
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

    private static <T, E extends Exception> void sneakyForEach(
            Stream<T> stream, CheckedConsumer<T, E> action) throws E {
        stream.forEach(Sneaker.sneakyConsumer(action));
    }

    private MoreStreams() {}
}
