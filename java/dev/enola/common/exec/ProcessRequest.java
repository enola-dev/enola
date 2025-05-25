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
 */
// TODO @Immutable
public record ProcessRequest(
        Path directory,
        ImmutableList<String> command,
        ImmutableMap<String, String> environment,
        InputStream in,
        OutputStream out,
        OutputStream err) {

    // TODO The in/out/err can't be here like this, because that makes it impossible to launch x2!
    //   So should it be a ... Supplier<InputStream/OutputStream> ? in/out/err
    //   Use a Handler, like https://github.com/brettwooldridge/NuProcess ?

    // TODO Should in/out/err be @Nullable or Optional<> ?

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
        private InputStream in;
        private OutputStream out;
        private OutputStream err;

        public Builder directory(Path directory) {
            if (directory != null) throw new IllegalStateException("directory already set");
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

        public Builder addEnvironment(Map<String, String> env) {
            environment.putAll(requireNonNull(env, "env"));
            return this;
        }

        public Builder in(InputStream in) {
            if (in != null) throw new IllegalStateException("in already set");
            this.in = requireNonNull(in, "in");
            return this;
        }

        public Builder out(OutputStream out) {
            if (out != null) throw new IllegalStateException("out already set");
            this.out = requireNonNull(out, "out");
            return this;
        }

        public Builder err(OutputStream err) {
            if (err != null) throw new IllegalStateException("err already set");
            this.err = requireNonNull(err, "err");
            return this;
        }

        public ProcessRequest build() {
            return new ProcessRequest(
                    directory, command.build(), environment.build(), in, out, err);
        }
    }
}
