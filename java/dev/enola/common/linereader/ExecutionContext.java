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
package dev.enola.common.linereader;

import com.google.common.collect.ImmutableMap;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * ExecutionContext provides access to the "current execution context", that is the environment, and
 * IN, OUT and ERR streams - with encodings (or, if none, then {@link
 * java.nio.charset.StandardCharsets#ISO_8859_1}, but NOT {@link
 * java.nio.charset.StandardCharsets#US_ASCII}). This is useful for any code which needs to access
 * input or provide output.
 */
public interface ExecutionContext {

    // TODO #Optimization: Allow in/out/err be @Nullable or Optional<> (for "discard")

    /**
     * Returns an immutable map of environment variables specific to this execution. These variables
     * are typically used to configure the process or task currently being executed. Prefer using
     * this over the static {@link System#getenv()} in applications which run multiple child
     * processes.
     *
     * @return An ImmutableMap of environment variables.
     */
    ImmutableMap<String, String> environment();

    /**
     * Returns the input stream for the current execution. This stream is typically used to feed
     * data into the executing process or task. Prefer using this over the static {@link System#in}
     * in applications which might process multiple incoming request connections.
     *
     * @return The InputStream for input.
     */
    InputStream input();

    /**
     * Returns the output stream for the current execution. This stream is typically used to capture
     * output from the executing process or task. Prefer using this over the static {@link
     * System#out} in applications which may output to various destinations in parallel.
     *
     * @return The OutputStream for output.
     */
    OutputStream output();

    /**
     * Returns the error stream for the current execution. This stream is typically used to capture
     * error output from the executing process or task. Prefer using this over the static {@link
     * System#err}.
     *
     * @return The OutputStream for errors.
     */
    OutputStream error();

    /**
     * Returns the character encoding to be used for the input stream. This charset must be used to
     * convert characters to bytes when reading from {@link #input()}.
     *
     * @return The Charset for input.
     */
    Charset inputCharset();

    /**
     * Returns the character encoding to be used for the output stream. This charset must be used to
     * convert bytes to characters when writing to {@link #output()}.
     *
     * @return The Charset for output.
     */
    Charset outputCharset();

    /**
     * Returns the character encoding to be used for the error stream. This charset must be used to
     * convert bytes to characters when writing to {@link #error()}.
     *
     * @return The Charset for errors.
     */
    Charset errorCharset();
}
