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
package dev.enola.common.exec;

import static com.google.common.base.Strings.emptyToNull;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

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
 * @param environment the environment variables for the process
 * @param in the input stream supplier for the process
 * @param out the output stream supplier for the process
 * @param err the error stream supplier for the process
 * @param mergeErrIntoOut whether to merge the error stream into the output stream
 */
public record ProcessRequest(
        Path directory,
        ImmutableList<String> command,
        ImmutableMap<String, String> environment,
        Supplier<InputStream> in,
        Supplier<OutputStream> out,
        Supplier<OutputStream> err,
        boolean mergeErrIntoOut) {

    // TODO Flip InputStream/OutputStream? To avoid unnecessary pumping!

    // TODO Use a "Handler", with ByteBuffer; like
    // https://brettwooldridge.github.io/NuProcess/apidocs/com/zaxxer/nuprocess/NuProcessHandler.html

    // TODO Integration with common.io.Resource

    // TODO #Optimization: Allow in/out/err be Supplier of @Nullable or Optional<> (for "discard")

    public ProcessRequest {
        requireNonNull(directory, "directory");
        requireNonNull(command, "command");
        requireNonNull(environment, "environment");
        requireNonNull(in, "in");
        requireNonNull(out, "out");
        requireNonNull(err, "err");
        if (command.isEmpty()) throw new IllegalArgumentException("command is empty");
    }

    public static class Builder {
        private Path directory;
        private final ImmutableList.Builder<String> command = new ImmutableList.Builder<>();
        private final ImmutableMap.Builder<String, String> environment =
                new ImmutableMap.Builder<>();
        private Supplier<InputStream> in;
        private Supplier<OutputStream> out;
        private Supplier<OutputStream> err;
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

        public Builder addEnvironment(String key, String value) {
            environment.put(
                    requireNonNull(emptyToNull(key), "key"), requireNonNull(value, "value"));
            return this;
        }

        /**
         * Add all environment variables from the given map.
         *
         * <p>Often used with {@link System#getenv()} to inherit the JVM process' environment.
         *
         * @param env a Map of key/values for the Environment
         * @return this
         */
        public Builder addEnvironment(Map<String, String> env) {
            environment.putAll(requireNonNull(env, "env"));
            return this;
        }

        public Builder in(Supplier<InputStream> in) {
            if (this.in != null) throw new IllegalStateException("in already set");
            this.in = requireNonNull(in, "in");
            return this;
        }

        public Builder out(Supplier<OutputStream> out) {
            if (this.out != null) throw new IllegalStateException("out already set");
            this.out = requireNonNull(out, "out");
            return this;
        }

        public Builder err(Supplier<OutputStream> err) {
            if (this.err != null) throw new IllegalStateException("err already set");
            this.err = requireNonNull(err, "err");
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
            return new ProcessRequest(
                    directory, command.build(), environment.build(), in, out, err, mergeErrIntoOut);
        }
    }
}
