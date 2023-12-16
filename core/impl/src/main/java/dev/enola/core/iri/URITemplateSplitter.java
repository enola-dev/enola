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

import com.github.fge.uritemplate.URITemplate;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Splits an URI based on a RFC 6570 Template. This is the "inverse" of {@link URITemplate}.
 * Reliably parsing URIs correctly is hard, see https://urlpattern.spec.whatwg.org (and note
 * https://github.com/teaconmc/urlpattern/issues/1). This class is intentionally limited, and only
 * fulfills the current needs of this project. It still has a lot of gaps. If you read this and need
 * it to do more and better, please improve it, along with its coverage in URITemplateTest!
 */
public class URITemplateSplitter {
    private final String template;
    private final List<String> keys;
    private final Pattern uriPattern;

    public URITemplateSplitter(String template) {
        this.template = template;
        var namesAndPattern = URITemplatePatternizer.toNamesAndPattern(template);
        this.keys = namesAndPattern.getKey();
        this.uriPattern = namesAndPattern.getValue();
    }

    public Optional<Map<String, String>> fromString(String uri) {
        var map = ImmutableMap.<String, String>builder();
        var matcher = uriPattern.matcher(uri);

        if (matcher.find()) {
            for (var name : keys) {
                String value = matcher.group(name);
                map.put(name, value);
            }
            return Optional.of(map.build());
        } else {
            return Optional.empty();
        }
    }
}
