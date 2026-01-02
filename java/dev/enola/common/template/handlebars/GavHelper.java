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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GavHelper implements Helper<Map<String, Object>> {

    @Override
    public CharSequence apply(Map<String, Object> context, Options options) throws IOException {
        var gavText = context.keySet().iterator().next();
        // TODO https://github.com/bazel-contrib/rules_jvm_external/issues/1452
        //   return "gav = " + gavText + ",\n";

        @SuppressWarnings("unchecked")
        List<String> exclusions = (List<String>) context.get("exclusions");

        var gav = GAV.parse(gavText);
        var text =
                """
                artifact = "%s",
                    exclusions = %s,
                    group = "%s",
                    version = "%s",\
                """
                        .formatted(
                                gav.artifactId(),
                                exclusions(exclusions),
                                gav.groupId(),
                                gav.version());
        return new Handlebars.SafeString(text);
    }

    private CharSequence exclusions(List<String> context) throws IOException {
        if (context == null || context.isEmpty()) {
            return "[]";
        }

        var sb = new StringBuilder();
        sb.append("[\n");
        for (var s : context) {
            sb.append("        \"").append(s).append("\",\n");
        }
        sb.append("    ]");
        return new com.github.jknack.handlebars.Handlebars.SafeString(sb.toString());
    }
}
