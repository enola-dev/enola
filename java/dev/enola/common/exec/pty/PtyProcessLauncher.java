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

import dev.enola.common.exec.ProcessLauncher;
import dev.enola.common.exec.ProcessRequest;
import dev.enola.common.exec.ProcessResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class PtyProcessLauncher implements ProcessLauncher {

    private final boolean console;
    private final boolean redirectErrorStream;

    public PtyProcessLauncher(boolean console, boolean redirectErrorStream) {
        this.console = console;
        this.redirectErrorStream = redirectErrorStream;
    }

    @Override
    public ProcessResponse execute(ProcessRequest request) {
        var started = Instant.now();
        var ctx = request.ctx().get();

        final var result = new CompletableFuture<Integer>();
        final var terminatedAtom = new AtomicReference<Optional<Instant>>(Optional.empty());

        try {
            // skipcq: JAVA-W1087
            new PtyRunner(
                            console,
                            request.directory(),
                            request.command().toArray(new String[0]),
                            ctx.environment(),
                            ctx.input(),
                            ctx.output(),
                            ctx.error(),
                            redirectErrorStream)
                    .process()
                    .onExit()
                    .whenComplete(
                            (process, throwable) -> {
                                terminatedAtom.set(Optional.of(Instant.now()));
                                if (process != null) result.complete(process.exitValue());
                                else result.completeExceptionally(throwable);
                            });

        } catch (IOException e) {
            result.completeExceptionally(e);
        }

        return new ProcessResponse() {
            @Override
            public CompletableFuture<Integer> async() {
                return result;
            }

            @Override
            public ProcessRequest request() {
                return request;
            }

            @Override
            public InputStream in() {
                return ctx.input();
            }

            @Override
            public OutputStream out() {
                return ctx.output();
            }

            @Override
            public OutputStream err() {
                return ctx.error();
            }

            @Override
            public Instant started() {
                return started;
            }

            @Override
            public Optional<Instant> terminated() {
                return terminatedAtom.get();
            }
        };
    }
}
