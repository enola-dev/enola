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

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ContextAwareThreadFactoryTest {

    @Test
    public void wrap() throws InterruptedException {
        var tf = new ContextAwareThreadFactory();
        try (var ctx = TLC.open()) {
            ctx.push(TestCtxKey.MAGIC, 123);

            var atomic = new AtomicInteger();
            Runnable runnable = () -> atomic.set(TLC.get(TestCtxKey.MAGIC));
            var thread = tf.newThread(runnable);
            thread.start();
            thread.join();

            assertThat(atomic.get()).isEqualTo(123);
        }
    }

    private enum TestCtxKey implements Context.Key<Integer> {
        MAGIC
    }
}
