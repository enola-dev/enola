/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import dev.enola.ai.mcp.cli.McpOptions;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;

import java.net.URI;
import java.util.List;

/** CLI Options related to AI shared between AiCommand, ServerCommand and Chat2Command. */
public class AiOptions {

    static final String DEFAULT_MODEL =
            "mocklm:Use%20--lm%20argument%20to%20configure%20default%20LLM";

    @CommandLine.Option(
            names = {"-m", "--lm", "--llm"},
            defaultValue = DEFAULT_MODEL,
            description = "Default Language Model AI URI; see https://docs.enola.dev/specs/aiuri/")
    @Nullable String defaultLanguageModelURI;

    @CommandLine.Option(
            names = {"-a", "--agents"},
            description = "URL/s of Agents; see https://docs.enola.dev/concepts/agent/")
    @Nullable List<URI> agentURIs;

    @Nullable
    @CommandLine.ArgGroup(exclusive = false)
    McpOptions mcpOptions;

    // TODO Agent Resources...

    public static class WithAgentName extends AiOptions {
        @CommandLine.Option(
                names = {"-d", "--default-agent"},
                description = "Agent Name; see https://docs.enola.dev/use/ai/")
        @Nullable String agentName;
    }
}
