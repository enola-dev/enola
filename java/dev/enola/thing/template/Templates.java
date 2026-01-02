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
package dev.enola.thing.template;

public final class Templates {

    static String unescapeURL(String escaped) {
        return escaped.replace("%7B", "{").replace("%7D", "}");
    }

    public static String dropVariableMarkers(String iri) {
        return iri.replace("{", "_").replace("}", "");
    }

    public static boolean hasVariables(String iri) {
        return iri.contains("{");
    }

    public static String convertToAnotherFormat(String iri, Format format) {
        return switch (format) {
            case Mustache -> iri.replace("{", "{{").replace("}", "}}");
            case HTML -> iri.replace("{", "<var>").replace("}", "</var>");
            case Star -> iri.replace("{", "[*").replace("}", "*]");
        };
    }

    public enum Format {
        Mustache,
        HTML,
        Star
    }

    private Templates() {}
}
