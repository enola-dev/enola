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

import static dev.enola.common.context.ContextsTest.TestContextLongKeys.OTHER;
import static dev.enola.common.context.ContextsTest.TestContextStringKeys.FOO;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ContextsTest {

    enum TestContextStringKeys implements Context.Key<String> {
        FOO,
        BAR
    }

    enum TestContextLongKeys implements Context.Key<Long> {
        OTHER
    }

    @Test
    public void empty() {
        assertThrows(IllegalStateException.class, () -> TLC.get(OTHER));
    }

    @Test
    public void one() {
        try (var ctx = TLC.open()) {
            ctx.push(FOO, "bar");
            String foo = TLC.get(FOO);
            assertThat(foo).isEqualTo("bar");
        }
    }

    @Test(expected = IllegalStateException.class)
    public void two() {
        try (var ctx = TLC.open()) {
            ctx.push(FOO, "bar");
            ctx.push(FOO, "baz");
        }
    }

    @Test
    public void nested() {
        try (var ctx1 = TLC.open()) {
            ctx1.push(FOO, "bar");
            assertThat(TLC.get(FOO)).isEqualTo("bar");

            try (var ctx2 = TLC.open()) {
                ctx2.push(FOO, "baz");
                assertThat(TLC.get(FOO)).isEqualTo("baz");
            }

            assertThat(TLC.get(FOO)).isEqualTo("bar");
        }
    }

    @Test
    public void exceptionWithContext() {
        try (var ctx1 = TLC.open()) {
            ctx1.push(FOO, "bar");

            try (var ctx2 = TLC.open()) {
                ctx2.push(FOO, "baz");

                try {
                    throw new ContextualizedException("TEST");

                } catch (ContextualizedException e) {
                    var stackTrace = stackTrace(e);
                    assertThat(stackTrace).contains("ContextsTest");
                    assertThat(stackTrace).contains("FOO");
                    assertThat(stackTrace).contains("bar");
                    assertThat(stackTrace).contains("baz");
                }
            }
        }
    }

    @Test
    public void exceptionsWithoutContext() {
        // Just to make sure that printStackTrace() doesn't throw a NullPointerException if no TLC
        stackTrace(new ContextualizedException("TEST"));
        stackTrace(new ContextualizedRuntimeException("TEST"));
    }

    @Test
    public void useAfterClose() {
        Context ctx = TLC.open();
        ctx.close();
        assertThrows(IllegalStateException.class, () -> ctx.get(OTHER));
    }

    @Test
    public void optionalNoTLC() {
        assertThat(TLC.optional(OTHER)).isEmpty();
    }

    @Test
    public void optionalNoKey() {
        try (var ctx = TLC.open()) {
            assertThat(TLC.optional(OTHER)).isEmpty();
        }
    }

    // TODO Truth's ThrowableSubject is missing throwable support; add it!
    private String stackTrace(Throwable e) {
        var sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
