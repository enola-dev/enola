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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableMap;

import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TestIO implements IO {

    // TODO This is wrong, because it keeps InputStream ctx.input() separate from List<String> input

    static final Charset CHARSET = UTF_8;

    private int line = 0;
    private final List<String> input;
    private final List<String> output = new ArrayList<>();
    private final ExecutionContext ctx;

    public TestIO(List<String> lines) {
        this.input = lines;
        this.ctx =
                new ExecutionContext() {
                    @Override
                    public ImmutableMap<String, String> environment() {
                        return ImmutableMap.of();
                    }

                    @Override
                    public InputStream input() {
                        var text = String.join("\n", input);
                        var bytes = text.getBytes(CHARSET);
                        return new ByteArrayInputStream(bytes);
                    }

                    @Override
                    public OutputStream output() {
                        throw new UnsupportedOperationException("TODO");
                    }

                    @Override
                    public OutputStream error() {
                        throw new UnsupportedOperationException("TODO");
                    }

                    @Override
                    public Charset inputCharset() {
                        return CHARSET;
                    }

                    @Override
                    public Charset outputCharset() {
                        return CHARSET;
                    }

                    @Override
                    public Charset errorCharset() {
                        return CHARSET;
                    }
                };
    }

    @Override
    public ExecutionContext ctx() {
        return ctx;
    }

    @Override
    public @Nullable String readLine() {
        if (line < input.size()) return input.get(line++);
        else return null;
    }

    @Override
    public @Nullable String readLine(String prompt) {
        printf(prompt);
        return readLine();
    }

    @Override
    public void printf(String format, Object... args) {
        output.add(String.format(format, args));
    }

    public List<String> getOutput() {
        return output;
    }
}
