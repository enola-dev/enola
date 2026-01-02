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
package dev.enola.common.string2long;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

public abstract class AbstractStringToLongBiMapTest {

    abstract StringToLongBiMap.Builder create();

    StringToLongBiMap fill() {
        var builder = create();

        var hello = builder.put("hello");
        assertThat(hello).isEqualTo(0);

        var world = builder.put("world");
        assertThat(world).isEqualTo(1);

        var hello2 = builder.put("hello");
        assertThat(hello2).isEqualTo(0); // NOT 2!

        return builder.build();
    }

    @Test
    public void size() {
        assertThat(fill().size()).isEqualTo(2); // NOT 3!
    }

    @Test
    public void symbols() {
        assertThat(fill().symbols()).containsExactly("hello", "world").inOrder();
    }

    @Test
    public void getUnknownID() {
        var e = assertThrows(IllegalArgumentException.class, () -> fill().get(2));
        assertThat(e).hasMessageThat().contains("2");
    }

    @Test
    public void longOrStringConsumerOK() {
        final boolean[] ok = new boolean[1];
        var map = fill();
        map.get(
                "world",
                new StringToLongBiMap.LongOrStringConsumer() {
                    @Override
                    public void longID(long id) {
                        assertThat(id).isEqualTo(1);
                        ok[0] = true;
                    }

                    @Override
                    public void string(String symbol) {
                        ok[0] = false;
                    }
                });
        assertThat(ok[0]).isTrue();
    }

    @Test
    public void longOrStringConsumerNOPE() {
        final boolean[] ok = new boolean[1];
        var map = fill();
        map.get(
                "dunno",
                new StringToLongBiMap.LongOrStringConsumer() {
                    @Override
                    public void longID(long id) {
                        ok[0] = false;
                    }

                    @Override
                    public void string(String symbol) {
                        assertThat(symbol).isEqualTo("dunno");
                        ok[0] = true;
                    }
                });
        assertThat(ok[0]).isTrue();
    }
}
