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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class Version {

    private static final String DEFAULT = "DEVELOPMENT";

    private static final String VERSION = load();

    private static String load() {
        var classLoader = Version.class.getClassLoader();
        try (var inputStream = classLoader.getResourceAsStream("VERSION")) {
            if (inputStream == null) return DEFAULT;
            var bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            return DEFAULT;
        }
    }

    public static String get() {
        return VERSION;
    }

    public static String gitUI() {
        return "https://github.com/enola-dev/enola/tree/" + VERSION;
    }

    public static boolean isKnown() {
        return !DEFAULT.equals(VERSION);
    }

    private Version() {}
}
