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

import org.jspecify.annotations.Nullable;

import java.util.concurrent.ThreadFactory;

public final class ContextAwareThreadFactory implements ThreadFactory {

    private final ThreadFactory delegate;

    public ContextAwareThreadFactory() {
        this(java.util.concurrent.Executors.defaultThreadFactory());
    }

    public ContextAwareThreadFactory(ThreadFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Context currentTLC = TLC.get();
        // TODO rm: if (currentTLC == null) throw new IllegalStateException("No TLC found?!");
        Thread thread = delegate.newThread(new RunnableWrapper(runnable, currentTLC));
        return thread;
    }

    private static class RunnableWrapper implements Runnable {
        private final Runnable delegate;
        private final @Nullable Context outerContext;

        public RunnableWrapper(Runnable delegate, @Nullable Context outerContext) {
            this.delegate = delegate;
            this.outerContext = outerContext;
        }

        @Override
        public void run() {
            Context currentTLC = TLC.get();
            if (currentTLC != null)
                throw new IllegalStateException(
                        "New Thread should not have a TLC already?! " + currentTLC);
            if (outerContext != null) TLC.setThreadLocalContext(outerContext);
            delegate.run();
        }
    }
}
