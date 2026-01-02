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
package dev.enola.common.template.handlebars;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.regex.Pattern;

// See also dev.enola.connect.maven.GAVR
record GAV(String groupId, String artifactId, String version) {

    // Stolen from org.eclipse.aether.artifact.DefaultArtifact
    private static final Pattern COORDINATE_PATTERN =
            Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?:([^: ]+)");

    static GAV parse(String gav) {
        var matcher = COORDINATE_PATTERN.matcher(gav);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid GAV coordinate: " + gav);
        }
        return new GAV(matcher.group(1), matcher.group(2), nullToEmpty(matcher.group(7)));
    }
}
