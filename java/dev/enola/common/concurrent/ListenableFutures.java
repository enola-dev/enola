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
package dev.enola.common.concurrent;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public final class ListenableFutures {

    public static <T> void whenComplete(
            ListenableFuture<T> future,
            BiConsumer<? super @Nullable T, ? super @Nullable Throwable> action) {
        Futures.addCallback(
                future,
                new FutureCallback<>() {
                    @Override
                    public void onSuccess(T result) {
                        action.accept(result, null);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        action.accept(null, t);
                    }
                },
                Executors.newListeningDirectExecutor());
    }

    private ListenableFutures() {}
}
