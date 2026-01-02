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
package dev.enola.common.exec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MockProcessLauncher implements ProcessLauncher {

    private CompletableFuture<Integer> result;

    private final byte[] out;
    private final byte[] err;

    private @Nullable ImmutableList<String> command;
    private @Nullable Path directory;
    private @Nullable ImmutableMap<String, String> env;

    public MockProcessLauncher(int exit, byte[] out, byte[] err) {
        this(CompletableFuture.completedFuture(exit), out, err);
    }

    public MockProcessLauncher(Throwable e, byte[] out, byte[] err) {
        this(CompletableFuture.failedFuture(e), out, err);
    }

    private MockProcessLauncher(CompletableFuture<Integer> result, byte[] out, byte[] err) {
        this.result = result;
        this.out = Arrays.copyOf(out, out.length);
        this.err = Arrays.copyOf(err, err.length);
    }

    @Override
    public ProcessResponse execute(ProcessRequest request) {
        var started = Instant.now();
        this.directory = request.directory();
        this.command = request.command();

        var ctx = request.ctx().get();
        this.env = ctx.environment();

        var in = ctx.input();
        var out = ctx.output();
        var err = ctx.error();

        try {
            out.write(this.out);
            err.write(this.err);

        } catch (IOException e) {
            if (this.result.isCompletedExceptionally()) throw new IllegalStateException();
            this.result = CompletableFuture.failedFuture(e);
        }

        var terminated = Instant.now();
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
                return in;
            }

            @Override
            public OutputStream out() {
                return out;
            }

            @Override
            public OutputStream err() {
                return err;
            }

            @Override
            public Instant started() {
                return started;
            }

            @Override
            public Optional<Instant> terminated() {
                return Optional.of(terminated);
            }
        };
    }

    public Path directory() {
        if (directory == null) throw new IllegalStateException("Not executed yet");
        return directory;
    }

    public ImmutableList<String> command() {
        if (command == null) throw new IllegalStateException("Not executed yet");
        return command;
    }

    public ImmutableMap<String, String> env() {
        if (env == null) throw new IllegalStateException("Not executed yet");
        return env;
    }
}
