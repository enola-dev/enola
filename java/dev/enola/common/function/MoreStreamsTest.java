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

import java.util.ArrayList;
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
        var list = new java.util.concurrent.CopyOnWriteArrayList<String>();
        MoreStreams.forEach(Stream.of("a", "b").parallel(), list::add);
        assertThat(list).containsExactlyInAnyOrder("a", "b");
    }

    @Test
    public void testForEachException() {
        var ex =
                assertThrows(
                        MyCheckedException.class,
                        () -> {
                            MoreStreams.forEach(
                                    Stream.of("a"),
                                    e -> {
                                        throw new MyCheckedException();
                                    });
                        });
        assertThat(ex).isInstanceOf(MyCheckedException.class);
    }

    @Test
    public void testMap() throws Exception {
        var list = MoreStreams.map(Stream.of("a", "b"), e -> e.toUpperCase()).toList();
        assertThat(list).containsExactly("A", "B");
    }

    @Test
    public void testMapException() {
        var ex =
                assertThrows(
                        MyCheckedException.class,
                        () -> {
                            MoreStreams.map(
                                    Stream.of("a"),
                                    e -> {
                                        throw new MyCheckedException();
                                    });
                        });
        assertThat(ex).isInstanceOf(MyCheckedException.class);
    }

    private static class MyCheckedException extends Exception {}
}
