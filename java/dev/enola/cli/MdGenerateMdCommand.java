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
        name = "generate-md",
        mixinStandardHelpOptions = true,
        description = "Resolves magic links and generates indexes for Markdown files")
public class MdGenerateMdCommand implements Callable<Integer> {

    @Parameters(
            index = "0",
            description = "Input directory containing Markdown files",
            paramLabel = "INPUT-DIR")
    Path inputDirectory;

    @Parameters(
            index = "1",
            description = "Output directory for resolved Markdown files",
            paramLabel = "OUTPUT-DIR")
    Path outputDirectory;

    @Override
    public Integer call() throws Exception {
        Markdown.md2md(inputDirectory, outputDirectory);
        return 0;
    }
}
