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
package dev.enola.common.concurrent;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorsTest {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorsTest.class);

    @Test
    public void tlc() throws ExecutionException, InterruptedException {
        try (var executor = Executors.newListeningSingleThreadExecutor("ExecutorsTest", LOG)) {
            try (var ctx = TLC.open()) {
                ctx.push(TestCtxKey.MAGIC, 123);

                var atomic = new AtomicInteger();
                Runnable runnable = () -> atomic.set(TLC.get(TestCtxKey.MAGIC));

                executor.submit(runnable).get();

                assertThat(atomic.get()).isEqualTo(123);
            }
        }
    }

    private enum TestCtxKey implements Context.Key<Integer> {
        MAGIC
    }
}
