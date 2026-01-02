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
package dev.enola.common.exec.pty;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import com.google.common.util.concurrent.*;

import dev.enola.common.concurrent.Executors;
import dev.enola.common.concurrent.ListenableFutures;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/** 🏞️ Stream Pumper ⛽. */
// TODO Factor out a generic class Worker
class StreamPumper implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(StreamPumper.class);

    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(7);
    public static final int BUFFER_SIZE = 4096;

    private final String name;
    private final InputStream is;
    private final OutputStream os;

    private final ListeningExecutorService executor;
    private final ListenableFuture<Void> pumpFuture;

    private boolean stop = false;

    StreamPumper(String prefix, InputStream is, OutputStream os) {
        this.name = prefix;
        this.is = is;
        this.os = os;

        this.executor = Executors.newListeningSingleThreadExecutor(prefix + "StreamPumper", LOG);
        pumpFuture = this.executor.submit(this::pump);
    }

    private Void pump() {
        int bytesRead;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            // We cannot use this.is.transferTo(this.os); because of !stop
            while (!stop && (bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                os.flush();
            }
        } catch (IOException e) {
            if (stop) {
                LOG.debug(
                        "StreamPumper '{}' caught IOException during shutdown (likely stream"
                                + " closed; fine): {}",
                        name,
                        e.getMessage());
            } else {
                LOG.error(
                        "StreamPumper '{}' encountered an unexpected I/O error during pumping: {}",
                        name,
                        e.getMessage(),
                        e);
            }
        }
        return null;
    }

    void whenComplete(BiConsumer<Void, @Nullable Throwable> action) {
        ListenableFutures.whenComplete(pumpFuture, action);
    }

    void waitFor(Duration timeout) throws InterruptedException {
        try {
            pumpFuture.get(NANOSECONDS.convert(timeout), NANOSECONDS);
        } catch (TimeoutException e) {
            LOG.error(
                    "StreamPumper '{}' did not terminate within timeout {} during await. Output"
                            + " might be lost.",
                    name,
                    timeout);
        } catch (ExecutionException e) {
            LOG.error(
                    "StreamPumper '{}' task threw another exception during await: {}",
                    name,
                    e.getCause() != null ? e.getCause().getMessage() : e.getMessage(),
                    e);
        }
    }

    @Override
    public void close() {
        stop = true;
        executor.shutdownNow();

        try {
            waitFor(DEFAULT_TIMEOUT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            LOG.warn("StreamPumper '{}' was interrupted during close cleanup.", name);
        }

        Executors.shutdownAndAwaitTermination(executor);
    }
}
