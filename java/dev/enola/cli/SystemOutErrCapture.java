/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utility to capture System.out and System.err in unit tests. Note that this doesn't work well
 * together with code which keeps references to System.out/err in a static, such as JUL; see
 * EnolaTest.
 */
// TODO Try if LogManager.getLogManager().reset(); could fix ^^^ this?
public class SystemOutErrCapture implements AutoCloseable {

    // TODO Compare with similar
    // https://github.com/Hakky54/console-captor/blob/master/src/main/java/nl/altindag/console/ConsoleCaptor.java

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final PrintStream originalOut;
    private final PrintStream originalErr;

    private final ByteArrayOutputStream outBAOS = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errBAOS = new ByteArrayOutputStream();

    private final PrintStream outBAOS_PS;
    private final PrintStream errBAOS_PS;

    public SystemOutErrCapture() {
        originalOut = System.out;
        originalErr = System.err;

        outBAOS_PS = new PrintStream(outBAOS, true, CHARSET);
        errBAOS_PS = new PrintStream(errBAOS, true, CHARSET);

        System.setOut(outBAOS_PS);
        System.setErr(errBAOS_PS);
    }

    public String getSystemOut() {
        return outBAOS.toString(CHARSET);
    }

    public String getSystemErr() {
        return errBAOS.toString(CHARSET);
    }

    public void clear() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        dump();
        outBAOS.reset();
        errBAOS.reset();
        System.setOut(outBAOS_PS);
        System.setErr(errBAOS_PS);
    }

    @Override
    public void close() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);
        dump();
    }

    private void dump() {
        System.out.print(getSystemOut());
        System.err.print(getSystemErr());
    }
}
