/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.data.iri.template;

import com.github.fge.uritemplate.URITemplate;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Splits an URI based on an RFC 6570 Template. This is the "inverse" of {@link URITemplate}.
 * Reliably parsing URIs correctly is hard, see <a href="https://urlpattern.spec.whatwg.org">WHATWG
 * URL Pattern</a>. This class is intentionally limited, and only fulfills the current needs of this
 * project. It still has a lot of gaps. If you read this and need it to do more and better, please
 * improve it, along with its coverage in URITemplateSplitterTest and URITemplateTest! (Or find some
 * existing library which does this? E.g.
 * [teaconmc/urlpattern](https://github.com/teaconmc/urlpattern/issues/1)).
 */
public class URITemplateSplitter {

    // TODO Must escape certain characters that are reserved in RegExp! TDD.

    private static final Pattern URI_TEMPLATE_PATTERN = Pattern.compile("\\{([^{}]+)\\}");

    private final String template;
    private final List<String> keys;
    private final Pattern pattern;
    private final int length;

    /** Transforms a RFC 6570 URI Template into a Regular Expression usable to "match" it. */
    public URITemplateSplitter(String template) {
        var lengther = new StringBuilder();
        var pattern = new StringBuilder("^");
        var pmatcher = URI_TEMPLATE_PATTERN.matcher(template);
        var lmatcher = URI_TEMPLATE_PATTERN.matcher(template);
        var keysBuilder = ImmutableList.<String>builder();
        while (pmatcher.find()) {
            lmatcher.find();
            var name = pmatcher.group(1);
            keysBuilder.add(name);

            String group;
            var p = pmatcher.end();
            if (p < template.length()) {
                var nextCharacter = template.subSequence(p, p + 1).charAt(0);
                group = "(?<" + name + ">[^" + nextCharacter + "]+)";
            } else {
                group = "(?<" + name + ">.+)";
            }
            pmatcher.appendReplacement(pattern, group);
            lmatcher.appendReplacement(lengther, "*");
        }
        pmatcher.appendTail(pattern);
        lmatcher.appendTail(lengther);
        pattern.append('$');

        this.template = template;
        this.keys = keysBuilder.build();
        try {
            this.pattern = Pattern.compile(pattern.toString());
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(template, e);
        }
        this.length = lengther.length();
    }

    public Optional<Map<String, String>> fromString(String uri) {
        var map = ImmutableMap.<String, String>builder();
        var matcher = pattern.matcher(uri);

        if (matcher.find()) {
            for (var name : keys) {
                String value = matcher.group(name);
                map.put(name, value);
            }
            // System.out.println("URITemplateSplitter matched '" + uri + "' to: " + pattern);
            return Optional.of(map.build());
        } else {
            return Optional.empty();
        }
    }

    public String getTemplate() {
        return template;
    }

    public List<String> getKeys() {
        return keys;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("template", template)
                .add("keys", keys)
                .add("pattern", pattern)
                .add("length", length)
                .toString();
    }
}
