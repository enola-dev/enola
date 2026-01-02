/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common;

import java.net.URI;

public record URILineColumnMessage(String message, URI uri, long line, long column) {

    public URILineColumnMessage(String message, URI uri, long line) {
        this(message, uri, line, -1);
    }

    public String format() {
        // TODO Have some way of globally configuring default output format
        return format(Format.GCC);
    }

    public String format(Format format) {
        return switch (format) {
            case GCC -> forGCC();
            case VS -> forVS();
            case JSON -> asJSON();
        };
    }

    public enum Format {
        GCC,
        VS,
        JSON
    }

    private String forGCC() {
        return uri() + ":" + line + optionalColumn(":") + ": " + message;
    }

    private String forVS() {
        return uri() + "(" + line + optionalColumn(",") + "): " + message;
    }

    private String asJSON() {
        var sb = new StringBuilder("{ \"file\": \"").append(uri);
        sb.append("\", \"line\": ").append(line);
        if (column > -1) {
            sb.append(" \"column\": ").append(column);
        }
        sb.append(" \"message\": ").append(message);
        sb.append(" }");
        return sb.toString();
    }

    private String optionalColumn(String separator) {
        if (column > -1) {
            return separator + column;
        } else return "";
    }
}
