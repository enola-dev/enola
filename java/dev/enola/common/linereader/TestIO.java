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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableMap;

import org.jspecify.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class TestIO implements IO {

    static final Charset CHARSET = UTF_8;

    private final BufferedReader inputReader;
    private final ByteArrayInputStream input;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final ExecutionContext ctx;

    public TestIO(List<String> lines) {
        var text = String.join("\n", lines);
        var bytes = text.getBytes(CHARSET);
        this.input = new ByteArrayInputStream(bytes);
        this.inputReader = new BufferedReader(new InputStreamReader(input, CHARSET));

        this.ctx =
                new ExecutionContext() {
                    @Override
                    public ImmutableMap<String, String> environment() {
                        return ImmutableMap.of();
                    }

                    @Override
                    public InputStream input() {
                        return input;
                    }

                    @Override
                    public OutputStream output() {
                        return output;
                    }

                    @Override
                    public OutputStream error() {
                        return output;
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
        // if (input.available() < 1) return null;
        try {
            if (!inputReader.ready()) return null;
            return inputReader.readLine();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public @Nullable String readLine(String prompt) {
        printf(prompt);
        return readLine();
    }

    @Override
    public void printf(String format, Object... args) {
        output.writeBytes(String.format(format, args).getBytes(ctx.outputCharset()));
    }

    // This signature is misleading, because it makes it appear as if we were tracking "line",
    // whereas of course this is actually not so; any notion of print-by-line is meaningless
    // and lost as soon as any printf() contains new line characters.
    //   public List<String> getOutput() {
    //     return  output.toString(ctx.outputCharset()).lines().toList(); }

    public String getOutput() {
        return output.toString(ctx.outputCharset());
    }
}
