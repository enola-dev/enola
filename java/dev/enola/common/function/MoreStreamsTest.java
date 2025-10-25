/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public final class MoreStreamsTest {

    @Test
    public void testForEachSeq() throws Exception {
        var list = new ArrayList<String>();
        MoreStreams.forEach(Stream.of("a", "b"), e -> list.add(e));
        assertThat(list).containsExactly("a", "b");
    }

    @Test
    public void testForEachParallel() throws Exception {
        var list = new CopyOnWriteArrayList<String>();
        MoreStreams.forEach(Stream.of("a", "b").parallel(), list::add);
        assertThat(list).containsExactly("a", "b");
    }

    @Test
    public void testForEachException() {
        assertThrows(
                MyCheckedException.class,
                () -> {
                    MoreStreams.forEach(
                            Stream.of("a"),
                            e -> {
                                throw new MyCheckedException();
                            });
                });
    }

    @Test
    public void testMap() throws Exception {
        var list = MoreStreams.map(Stream.of("a", "b"), e -> e.toUpperCase()).toList();
        assertThat(list).containsExactly("A", "B");
    }

    @Test
    public void testMapParallel() throws Exception {
        var list = MoreStreams.map(Stream.of("a", "b").parallel(), String::toUpperCase).toList();
        assertThat(list).containsExactly("A", "B");
    }

    @Test
    public void testMapException() {
        assertThrows(
                MyCheckedException.class,
                () -> {
                    MoreStreams.map(
                                    Stream.of("a"),
                                    e -> {
                                        throw new MyCheckedException();
                                    })
                            .toList();
                });
    }

    @Test
    public void testMapParallelException() {
        assertThrows(
                MyCheckedException.class,
                () ->
                        MoreStreams.map(
                                        Stream.of("a", "b").parallel(),
                                        s -> {
                                            throw new MyCheckedException();
                                        })
                                .toList());
    }

    @Test
    public void testToIterable() {
        Iterable<String> iterable = MoreStreams.toIterable(Stream.of("a", "b"));
        assertThat(iterable).containsExactly("a", "b").inOrder();
    }

    @Test
    public void testToIterableSingleUse() {
        Iterable<String> iterable = MoreStreams.toIterable(Stream.of("a", "b"));
        assertThat(iterable).containsExactly("a", "b");
        assertThrows(IllegalStateException.class, iterable::iterator);
    }

    @Test
    public void testToIterableIsClosed() throws IOException {
        var closed = new java.util.concurrent.atomic.AtomicBoolean(false);
        Stream<String> stream = Stream.of("a", "b").onClose(() -> closed.set(true));
        try (var ignored = MoreStreams.toIterable(stream)) {}
        assertThat(closed.get()).isTrue();
    }

    private static class MyCheckedException extends Exception {}
}
