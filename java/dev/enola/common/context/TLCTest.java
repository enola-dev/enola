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
package dev.enola.common.context;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class TLCTest {

    @Test
    public void empty() {
        assertThrows(IllegalStateException.class, () -> TLC.get("whatever"));
    }

    @Test
    public void one() {
        try (var ctx = TLC.open()) {
            assertThat(ctx.get("unset")).isNull();
            ctx.push("foo", "bar");
            assertThat(TLC.get("foo")).isEqualTo("bar");
            assertThat(ctx.get("unset")).isNull();
        }
    }

    @Test
    public void nested() {
        try (var ctx1 = TLC.open()) {
            ctx1.push("foo", "bar");
            assertThat(TLC.get("foo")).isEqualTo("bar");

            try (var ctx2 = TLC.open()) {
                ctx2.push("foo", "baz");
                assertThat(TLC.get("foo")).isEqualTo("baz");
            }

            assertThat(TLC.get("foo")).isEqualTo("bar");
        }
    }

    @Test
    public void useAfterClose() {
        Context ctx = TLC.open();
        ctx.close();
        assertThrows(IllegalStateException.class, () -> ctx.get("whatever"));
    }
}
