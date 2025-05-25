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
package dev.enola.common.exec.pty;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Demo {

    // TODO Fix the (ugly) "echo" which "re-displays" all Fish shell input again

    public static void main(String[] args) throws IOException, InterruptedException {
        // System.exit(ptyRunner());
        System.exit(pty4j());
    }

    // For https://github.com/JetBrains/pty4j/issues/170
    static int pty4j() throws IOException, InterruptedException {
        String[] cmd = {"/bin/sh", "-l"};
        Map<String, String> env = new HashMap<>(System.getenv());
        if (!env.containsKey("TERM")) env.put("TERM", "xterm-256color");
        PtyProcess process = new PtyProcessBuilder().setCommand(cmd).setEnvironment(env).start();

        OutputStream os = process.getOutputStream();
        // new StreamPumper("In", System.in, os);
        new SimpleStreamPumper(System.in, os);

        InputStream is = process.getInputStream();
        // new StreamPumper("Out", is, System.out);
        new SimpleStreamPumper(is, System.out);

        return process.waitFor();
    }

    // Just for https://github.com/JetBrains/pty4j/issues/170
    static class SimpleStreamPumper extends Thread {
        SimpleStreamPumper(InputStream is, OutputStream os) {
            super(
                    () -> {
                        try {
                            int b;
                            while ((b = is.read()) != -1) {
                                os.write(b);
                                os.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            start();
        }
    }

    static int ptyRunner() throws IOException {
        int result;
        // TODO Read from $SHELL (and use cmd.exe on Windows)
        String[] cmd = {"/usr/bin/fish", "-l"};
        System.out.println("Starting: " + String.join(" ", cmd));
        try (var runner =
                new PtyRunner(
                        true,
                        Path.of("."),
                        cmd,
                        System.getenv(),
                        System.in,
                        System.out,
                        System.err)) {
            // System.out.println("Running, and awaiting exit of: " + String.join(" ", cmd));
            result = runner.waitForExit();
        }
        System.out.println("PTY demo exits!");
        return result;
    }
}
