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
package dev.enola.cli;

import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class CLI {

    private final String[] args;
    private final CommandLine commandLine;
    private StringWriter out;
    private StringWriter err;

    private Integer exitCode;

    public CLI(String[] args, CommandLine commandLine) {
        this.commandLine = commandLine;
        this.args = args;
    }

    public CLI setOut(PrintWriter out) {
        commandLine.setOut(out);
        return this;
    }

    public CLI setErr(PrintWriter err) {
        commandLine.setErr(err);
        return this;
    }

    public void setOutAndErrStrings() {
        out = new StringWriter();
        setOut(new PrintWriter(out));

        err = new StringWriter();
        setErr(new PrintWriter(err));
    }

    public String getOutString() {
        commandLine.getOut().flush();
        out.flush();
        return out.toString();
    }

    public String getErrString() {
        commandLine.getErr().flush();
        err.flush();
        return err.toString();
    }

    public int execute() {
        this.exitCode = commandLine.execute(args);
        return exitCode;
    }

    public int exitCode() {
        if (exitCode == null) {
            throw new IllegalStateException("Must execute() first!");
        }
        return exitCode;
    }

    @Override
    public String toString() {
        return "CLI{" + "args=" + Arrays.toString(args) + ", exitCode=" + exitCode + '}';
    }
}
