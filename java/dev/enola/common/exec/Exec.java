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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

public final class Exec {

    // TODO Flesh out this higher-level ProcessLauncher, to make it more like:
    //   - https://github.com/vorburger/ch.vorburger.exec
    //   - https://github.com/zeroturnaround/zt-exec

    // TODO Return something fluid with a logExit() etc.

    public static void run(
            ProcessLauncher launcher,
            Map<String, String> env,
            Path directory,
            InputStream in,
            OutputStream out,
            String... command) {

        var imap = ImmutableMap.copyOf(env);
        var ctx = new ExecutionContextImpl(imap, in, out, out, UTF_8, UTF_8, UTF_8);
        var request = new ProcessRequest(directory, ImmutableList.copyOf(command), () -> ctx, true);

        launcher.execute(request).async().handle(new LoggingExitConsumer(command[0]));
    }

    private Exec() {}
}
