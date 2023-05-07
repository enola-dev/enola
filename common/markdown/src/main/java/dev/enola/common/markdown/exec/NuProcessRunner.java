/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.common.markdown.exec;

import com.google.common.base.Charsets;
import com.zaxxer.nuprocess.NuProcessBuilder;
import com.zaxxer.nuprocess.codec.NuAbstractCharsetHandler;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CoderResult;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

class NuProcessRunner implements Runner {
    @Override
    public int exec(Path dir, List<String> command, Appendable output, Duration timeout)
            throws Exception {
        AppendingHandler handler = new AppendingHandler(output);
        NuProcessBuilder pb = new NuProcessBuilder(handler, command);
        pb.setCwd(dir);
        var process = pb.start(); // NOT pb.run();
        process.wantWrite();
        process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
        if (handler.exception != null) throw handler.exception;
        if (handler.exitCode == null) {
            throw new IllegalArgumentException("Could not exec, no exitCode: " + command);
        }
        return handler.exitCode;
    }

    // TODO Upstream (something like) this to https://github.com/brettwooldridge/NuProcess ?
    static class AppendingHandler extends NuAbstractCharsetHandler {
        private final Appendable appendable;
        Integer exitCode;
        Exception exception;

        protected AppendingHandler(Appendable appendable) {
            // UTF-8 is obviously just an assumption... TODO how to determine it safely?!
            super(Charsets.UTF_8);
            this.appendable = appendable;
        }

        @Override
        public void onExit(int exitCode) {
            this.exitCode = exitCode;
        }

        @Override
        protected void onStdoutChars(CharBuffer buffer, boolean closed, CoderResult coderResult) {
            onChars(buffer, closed, coderResult);
        }

        @Override
        protected void onStderrChars(CharBuffer buffer, boolean closed, CoderResult coderResult) {
            onChars(buffer, closed, coderResult);
        }

        private void onChars(CharBuffer buffer, boolean closed, CoderResult coderResult) {
            if (coderResult != CoderResult.UNDERFLOW) {
                System.err.println(coderResult);
                try {
                    coderResult.throwException();
                } catch (CharacterCodingException | RuntimeException e) {
                    exception = e;
                }
            }
            if (!closed) {
                try {
                    // TODO FIXME How-to CharBuffer ?!
                    appendable.append(buffer);
                    // StringBuffer sb = new StringBuffer();
                    // sb.append(buffer);
                    // appendable.append(sb);
                    // System.out.print(sb);
                } catch (IOException e) {
                    exception = e;
                }
            }
        }
    }
}
