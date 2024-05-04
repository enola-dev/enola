/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
import picocli.CommandLine.Option;

import java.net.URI;

public abstract class CommandWithModelAndOutput extends CommandWithModel {

    // Default command output destination is STDOUT.
    // NB: "fd:1" normally (in ResourceProviders) is FileDescriptorResource,
    // but CommandWithEntityID "hacks" this and uses WriterResource, for "testability".
    protected static final String DEFAULT_OUTPUT = "fd:1";
    protected static final URI DEFAULT_OUTPUT_URI = URI.create(DEFAULT_OUTPUT);

    @Option(
            names = {"--output", "-o"},
            required = true,
            defaultValue = DEFAULT_OUTPUT,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "URI of where to write output (of get or docgen)")
    URI output;
}
