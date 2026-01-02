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

import static com.google.common.truth.Truth.assertThat;

import com.github.fge.uritemplate.URITemplate;
import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import java.util.Map;

public class URITemplateTest {

    // TODO Use https://github.com/uri-templates/uritemplate-test as a test suite
    // TODO Test query parameters % encoding, see https://en.m.wikipedia.org/wiki/URI_Template
    // TODO Test that query parameters order doesn't matter
    // TODO Test extra unused query parameters
    // TODO Test support of "nested" Map structs (it won't work as-is)
    // TODO Add to https://github.com/uri-templates/uritemplate-spec/wiki/Implementations
    // TODO Test type of entry value, and fully support real nested and not just flat maps

    @Test
    public void testSimpleURITemplates() throws Exception {
        check(
                "http://example.com/people/{firstName}-{lastName}/SSN",
                "http://example.com/people/Michael-Vorburger/SSN",
                ImmutableMap.of("firstName", "Michael", "lastName", "Vorburger"));

        check("thing/{name}", "thing/abc", ImmutableMap.of("name", "abc"));

        // Something that's not actually really an URI should ideally work as well, but does not:
        // check("Greeting #{NUMBER}", "Greeting #42", ImmutableMap.of("NUMBER", "42"));
    }

    private void check(String template, String expected, Map<String, Object> map) throws Exception {
        // Test generating an URI from a Template
        var uriTemplate = new URITemplate(template);
        var uri = uriTemplate.toString(VariableMaps.from(map));
        assertThat(uri).isEqualTo(expected);

        // Test decomposing an URI
        var uriTemplateSplitter = new URITemplateSplitter(template);
        var split = uriTemplateSplitter.fromString(uri).get();
        assertThat(split).isEqualTo(map);
    }
}
