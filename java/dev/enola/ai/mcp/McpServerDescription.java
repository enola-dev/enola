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
package dev.enola.ai.mcp;

import java.net.URI;

/**
 * Description of an MCP Server.
 *
 * <p>This is read from an MCP server.
 *
 * @see McpServerConnectionsConfig
 * @see McpServerMetadata
 */
// TODO Such a class probably already exists in the MCP SDK?
//   But is a "struct" that can serialized to JSON/YAML?
public class McpServerDescription {
    // implements Identifiable ?

    /** See {@link McpServerConnectionsConfig.ServerConnection#origin}. */
    public URI id;

    // TODO Tools, Arguments, Descriptions, etc.
}
