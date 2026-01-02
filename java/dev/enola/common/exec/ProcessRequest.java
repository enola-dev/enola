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

import static java.util.Objects.requireNonNull;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

import dev.enola.common.linereader.ExecutionContext;

import java.nio.file.Path;

/**
 * ProcessRequest describes a request for the Operating System to run a program from an image in an
 * executable file.
 *
 * <p>This is not a {@link Process}, but what is needed to create one process (or several).
 *
 * <p>{@link ProcessBuilder} is similar to this. But that's tied to the JVM's builtin process
 * launcher - while this may use other means to execute the command. This intentionally has no
 * start() method, because it is passed to a {@link ProcessLauncher} to actually run it.
 *
 * @param directory the working directory for the process
 * @param command the command to execute
 * @param ctx the {@link ExecutionContext}
 * @param mergeErrIntoOut whether to merge the error stream into the output stream
 */
public record ProcessRequest(
        Path directory,
        ImmutableList<String> command,
        Supplier<ExecutionContext> ctx,
        boolean mergeErrIntoOut) {

    // TODO ExecutionContext: Flip InputStream/OutputStream? To avoid unnecessary pumping!

    // TODO Use a "Handler", with ByteBuffer; like
    // https://brettwooldridge.github.io/NuProcess/apidocs/com/zaxxer/nuprocess/NuProcessHandler.html

    // TODO Integration with common.io.Resource; https://docs.enola.dev/use/fetch/#exec

    public ProcessRequest {
        requireNonNull(directory, "directory");
        requireNonNull(command, "command");
        requireNonNull(ctx, "ctx");
        if (command.isEmpty()) throw new IllegalArgumentException("command is empty");
    }

    public static class Builder {
        private Path directory;
        private final ImmutableList.Builder<String> command = new ImmutableList.Builder<>();
        private Supplier<ExecutionContext> ctx;
        private boolean mergeErrIntoOut = false;

        public Builder directory(Path directory) {
            if (this.directory != null) throw new IllegalStateException("directory already set");
            this.directory = requireNonNull(directory, "directory");
            return this;
        }

        public Builder command(String... command) {
            this.command.add(command);
            return this;
        }

        public Builder executionContext(Supplier<ExecutionContext> ctx) {
            if (this.ctx != null) throw new IllegalStateException("ctx already set");
            this.ctx = requireNonNull(ctx, "ctx");
            return this;
        }

        public Builder mergeErrIntoOut(boolean mergeErrIntoOut) {
            this.mergeErrIntoOut = mergeErrIntoOut;
            return this;
        }

        public Builder mergeErrIntoOut() {
            return mergeErrIntoOut(true);
        }

        public ProcessRequest build() {
            return new ProcessRequest(directory, command.build(), ctx, mergeErrIntoOut);
        }
    }
}
