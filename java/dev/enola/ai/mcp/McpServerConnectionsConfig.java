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

import dev.enola.common.io.object.WithSchema;

import io.modelcontextprotocol.spec.McpSchema;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration how to connect to an MCP Server.
 *
 * <p>This is inspired by e.g. Claude's or VSC's mcp.json etc.
 *
 * @see McpServerMetadata
 */
public class McpServerConnectionsConfig extends WithSchema {
    // implements Identifiable ?  String id() { return origin.toString(); }

    /** Origin of configuration; e.g. file:/.../mcp.yaml, or something like that. */
    // TODO extends WithOrigin implements HasOrigin; and set it in ObjectReader
    public URI origin;

    public Map<String, ServerConnection> servers = new HashMap<>();

    // public final Map<String, Input> inputs = new HashMap<>();

    public static class ServerConnection {

        public ServerConnection() {}

        public ServerConnection(ServerConnection other) {
            this.origin = other.origin;
            this.docs = other.docs;
            this.type = other.type;
            this.command = other.command;
            this.args = other.args;
            this.env = other.env;
            this.url = other.url;
            this.headers = other.headers;
            this.timeout = other.timeout;
            this.roots = other.roots;
            this.log = other.log;
        }

        /**
         * Origin of configuration; e.g. file:/.../mcp.yaml#github, or something like that. This is
         * automatically set by the {@link McpLoader}. (This originally used to be confused with
         * {@link #docs}.)
         */
        // TODO extends WithOrigin implements HasOrigin; and set it in ObjectReader
        public @Nullable URI origin;

        /**
         * Server's homepage or GitHub repo, or whatever uniquely identifies it.
         *
         * <p>There could be (external) sameAs equivalencies defined for disambiguation.
         *
         * <p>This is NOT its "package", so don't put a NPM or Container Image etc. 'PURL' here.
         */
        // TODO Use another name than 'origin' here, to avoid confusion with the outer origin above?
        public String docs;

        public enum Type {
            stdio,
            http, // TODO http_sse? streamable_http?
        }

        public Type type = Type.stdio;

        // STDIO; like io.modelcontextprotocol.client.transport.ServerParameters
        public String command;
        public List<String> args = new ArrayList<>();
        public Map<String, String> env = new HashMap<>();

        // HTTP; like com.google.adk.tools.mcp.SseServerParameters
        public String url;
        public Map<String, String> headers = new HashMap<>();
        public Duration timeout;

        public boolean roots = false;

        public McpSchema.LoggingLevel log = McpSchema.LoggingLevel.WARNING;
    }

    /*
        public static class Input {
            public enum Type {
                http
            }

            public Type type;
            public String id;
            public String description;
            public boolean password;
        }
    */
    // TODO public Optional<String> validate() {}
}
