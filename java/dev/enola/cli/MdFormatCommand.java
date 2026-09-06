/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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

import dev.enola.common.markdown.Markdown;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "format",
        aliases = {"fmt"},
        mixinStandardHelpOptions = true,
        description = "Formats Markdown files in place")
public class MdFormatCommand implements Callable<Integer> {

    @Parameters(
            index = "0",
            description = "Directory or file containing Markdown files to format",
            paramLabel = "PATH")
    Path path;

    @Override
    public Integer call() throws Exception {
        Markdown.canonicalize(path);
        return 0;
    }
}
