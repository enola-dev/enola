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
package dev.enola.cli;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;

/** CLI Options related to AI shared between ServerCommand and (TBD) AdkChatCommand. */
public class AiOptions {

    static final String DEFAULT_MODEL =
            "mocklm:Use%20--lm%20argument%20to%20configure%20default%20LLM";

    @CommandLine.Option(
            names = {"-m", "--lm", "--llm"},
            defaultValue = DEFAULT_MODEL,
            description = "Default Language Model AI URI; see https://docs.enola.dev/specs/aiuri/")
    @Nullable String defaultLanguageModelURI;

    // TODO Agent Resources...
}
