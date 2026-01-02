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
package dev.enola.ai.mcp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Metadata about an MCP Server.
 *
 * <p>This cannot be read from an MCP Server itself, but is "human (or LLM...) authored".
 *
 * @see McpServerConnectionsConfig
 */
public class McpServerMetadata {
    // implements Identifiable ?

    // TODO https://schema.org/SoftwareApplication

    /** See {@link McpServerConnectionsConfig.ServerConnection#origin}. */
    public URI id;

    public String description;

    public final List<String> tags = new ArrayList<>();
}
