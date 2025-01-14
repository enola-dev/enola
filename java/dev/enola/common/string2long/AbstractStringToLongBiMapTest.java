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
package dev.enola.common.string2long;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public abstract class AbstractStringToLongBiMapTest {

    abstract StringToLongBiMap.Builder create();

    StringToLongBiMap fill() {
        var builder = create();

        var hello = builder.put("hello");
        assertThat(hello).isEqualTo(0);

        var world = builder.put("world");
        assertThat(world).isEqualTo(1);

        return builder.build();
    }

    @Test
    public void get() {
        var map = fill();
        assertThat(map.get("hello")).isEqualTo(0);
        assertThat(map.get("world")).isEqualTo(1);
    }

    @Test
    public void size() {
        assertThat(fill().size()).isEqualTo(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUnknownSymbol() {
        fill().get("dunno");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUnknownID() {
        fill().get(2);
    }
}
