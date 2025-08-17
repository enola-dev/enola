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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration how to connect to an MCP Server.
 *
 * <p>This is inspired by e.g. Claude's or VSC's mcp.json etc.
 *
 * @see McpServerDescription
 * @see McpServerMetadata
 */
public class McpServerConnectionsConfig {
    // implements Identifiable ?  String id() { return origin.toString(); }

    // TODO roots? Or does that not belong here?

    /** Origin of configuration; e.g. file:/.../mcp.yaml, or something like that. * */
    public URI origin;

    public final Map<String, ServerConnection> servers = new HashMap<>();
    public final Map<String, Input> inputs = new HashMap<>();

    public static class ServerConnection {
        /**
         * Server's homepage or GitHub repo, or whatever uniquely identifies it.
         *
         * <p>There could be (external) sameAs equivalencies defined for disambiguation.
         *
         * <p>This is NOT its "package", so don't put a NPM or Container Image etc. 'PURL' here.
         */
        public URI origin;

        public enum Type {
            stdio,
            http, // TODO http_sse? streamable_http?
        }

        public Type type = Type.stdio;

        // STDIO
        public String command;
        public final List<String> args = new ArrayList<>();
        public final Map<String, String> env = new HashMap<>();

        // HTTP
        public String url;
        public final Map<String, String> headers = new HashMap<>();
    }

    public static class Input {
        public enum Type {
            http
        }

        public Type type;
        public String id;
        public String description;
        public boolean password;
    }

    // TODO public Optional<String> validate() {}
}
