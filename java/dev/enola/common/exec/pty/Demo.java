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

import java.io.IOException;

public class Demo {

    // TODO Fix the (ugly) "echo" which "re-displays" all Fish shell input again

    public static void main(String[] args) throws IOException {
        int result;
        String[] cmd = {"/usr/bin/fish", "-l"};
        System.out.println("Starting: " + String.join(" ", cmd));
        try (var runner =
                new PtyRunner(true, cmd, System.getenv(), System.in, System.out, System.err)) {
            // System.out.println("Running, and awaiting exit of: " + String.join(" ", cmd));
            result = runner.waitForExit();
        }
        System.out.println("PTY demo exits!");
        System.exit(result);
    }
}
