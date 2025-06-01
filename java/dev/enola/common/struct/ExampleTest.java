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
package dev.enola.common.struct;

import org.junit.Test;

import java.util.List;

public class ExampleTest {

    @Test
    public void empty() {
        // var o = ExtendedExampleBuilder().build();
        // assertThat(o.text()).isNull();
        // assertThat(o.number()).isNull();
        // assertThat(o.instant()).isNull();
        // assertThat(o.example()).isNull();

        // assertThat(o.examplesInOrder()).isEmpty();
        // assertThat(o.examplesBag()).isEmpty();
        // assertThat(o.map()).isEmpty();

        // assertThat(o.bigDecimal()).isNull();
        // assertThat(o.url()).isNull();
    }

    @Test
    public void set() {
        // var o = ExtendedExampleBuilder().text("hello, world").build();
        // assertThat(o.text()).isEqualTo("hello, world");
    }

    @Test
    public void hashCodeEquals() {
        // var o1 = ExtendedExampleBuilder().text("hello, world").build();
        // var o2 = ExtendedExampleBuilder().text("hello, world").build();
        // assertThat(o1).isEqualTo(o2);
        // assertThat(o1.hashCode()).isEqualTo(o2.hashCode());
    }

    @Test
    public void testToString() {
        // var o = ExtendedExampleBuilder().text("hello, world").build();
        // assertThat(o.toString()).contains("hello, world");
        // assertThat(o.toString()).contains("text");
    }

    @Test
    public void guavaImmutableList() {
        // var o1 = ExtendedExampleBuilder().build();
        // var o2 = ExtendedExampleBuilder().addExamplesInOrder(o1).build();
        // assertThat(o.examplesInOrder()).containsExactly(o1);
        // assertThat(o.examplesInOrder()).isInstanceOf(ImmutableList.class);
    }
}
