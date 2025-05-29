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
package dev.enola.common.linereader;

import com.google.common.collect.ImmutableMap;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * SystemInOutIO is an {@link IO} implementation based on {@link System#in} and {@link System#out}.
 */
public class SystemInOutIO implements IO {
    private static final Logger LOG = LoggerFactory.getLogger(SystemInOutIO.class);

    private final ImmutableMap<String, String> env;

    // TODO Charset should not be hard-coded to defaultCharset, but ... from Context? Console?!
    // TODO Java 25+ Use System.in.charset() ["stdin.encoding"]
    // see https://github.com/openjdk/jdk/pull/25271
    // see https://github.com/jline/jline3/issues/1282
    private final Charset inCharset = Charset.defaultCharset();

    private final BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in, inCharset));

    public SystemInOutIO() {
        this(System.getenv());
    }

    public SystemInOutIO(Map<String, String> env) {
        this.env = ImmutableMap.copyOf(env);
    }

    @Override
    public @Nullable String readLine() {
        try {
            var line = reader.readLine();
            if (line != null) System.out.println(line); // echo!
            return line;
        } catch (IOException e) {
            LOG.warn("readLine() from STDIN, without System.console(), failed", e);
            return null;
        }
    }

    @Override
    public @Nullable String readLine(String prompt) {
        printf(prompt);
        return readLine();
    }

    @Override
    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    @Override
    public ExecutionContext ctx() {
        return new ExecutionContext() {
            @Override
            public ImmutableMap<String, String> environment() {
                return env;
            }

            @Override
            public InputStream input() {
                return System.in;
            }

            @Override
            public OutputStream output() {
                return System.out;
            }

            @Override
            public OutputStream error() {
                return System.err;
            }

            @Override
            public Charset inputCharset() {
                return inCharset;
            }

            @Override
            public Charset outputCharset() {
                return System.out.charset();
            }

            @Override
            public Charset errorCharset() {
                return System.err.charset();
            }
        };
    }
}
