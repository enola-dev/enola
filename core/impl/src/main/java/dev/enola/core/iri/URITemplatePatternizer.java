/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.core.iri;

import com.google.common.collect.ImmutableList;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Transforms a RFC 6570 URI Template into a Regular Expression usable to "match" it. This class as
 * currently implemented still has a lot of limitations; see also {@link URITemplateSplitter}.
 */
final class URITemplatePatternizer {

    // TODO Must escape certain characters that are reserved in RegExp! TDD.

    private static final Pattern URI_TEMPLATE_PATTERN = Pattern.compile("\\{([^{}]+)\\}");

    private URITemplatePatternizer() {}

    public static Entry<List<String>, Pattern> toNamesAndPattern(String template) {
        var sb = new StringBuilder();
        var matcher = URI_TEMPLATE_PATTERN.matcher(template);
        var keysBuilder = ImmutableList.<String>builder();
        while (matcher.find()) {
            var name = matcher.group(1);
            keysBuilder.add(name);

            String group;
            var p = matcher.end();
            if (p < template.length()) {
                var nextCharacter = template.subSequence(p, p + 1).charAt(0);
                group = "(?<" + name + ">[^" + nextCharacter + "]+)";
            } else {
                group = "(?<" + name + ">.+)";
            }
            matcher.appendReplacement(sb, group);
        }
        matcher.appendTail(sb);

        return new SimpleEntry(keysBuilder.build(), Pattern.compile(sb.toString()));
    }
}
