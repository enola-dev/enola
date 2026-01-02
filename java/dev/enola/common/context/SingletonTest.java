/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.SingletonRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class SingletonTest {

    // The Singleton(s) would IRL be defined somewhere else than inside the *Test.
    static Singleton<String> HELLO_SINGLETON = new Singleton<>() {};

    static Singleton<Integer> THE_NUMBER = new Singleton<>() {};
    @ClassRule public static final SingletonRule r = $(THE_NUMBER.set(43));

    @Test
    public void staticSingleton() {
        HELLO_SINGLETON.set("hello, world");
        assertThat(HELLO_SINGLETON.get()).isEqualTo("hello, world");

        HELLO_SINGLETON.reset();
        HELLO_SINGLETON.set("hi");
        assertThat(HELLO_SINGLETON.get()).isEqualTo("hi");

        // Setting it again to the same value is acceptable (and a NOOP)
        HELLO_SINGLETON.set("hi");
        // But setting it to another value (without reset()) causes an IllegalStateException
        Assert.assertThrows(IllegalStateException.class, () -> HELLO_SINGLETON.set("bye"));
    }

    @Test
    public void singletonViaRule1() {
        assertThat(THE_NUMBER.get()).isEqualTo(43);
    }

    @Test
    public void singletonViaRule2() {
        assertThat(THE_NUMBER.get()).isEqualTo(43);
    }
}
