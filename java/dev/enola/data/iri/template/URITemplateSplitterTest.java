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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class URITemplateSplitterTest {

    @Test
    public void example() throws Exception {
        check(
                "http://example.com/people/{firstName}-{lastName}/SSN",
                "^http://example.com/people/(?<firstName>[^-]+)-(?<lastName>[^/]+)/SSN$",
                "firstName",
                "lastName");

        check("thing/{name}", "^thing/(?<name>.+)$", "name");
    }

    private void check(String template, String regexp, String... names) {
        var namesAndPattern = new URITemplateSplitter(template);

        var pattern = namesAndPattern.getPattern();
        assertThat(pattern.toString()).isEqualTo(regexp);

        var keys = namesAndPattern.getKeys();
        assertThat(keys).isEqualTo(newArrayList(names));
    }

    @Test
    public void length() throws Exception {
        assertThat(new URITemplateSplitter("").getLength()).isEqualTo(0);
        assertThat(new URITemplateSplitter("/hello").getLength()).isEqualTo(6);
        assertThat(new URITemplateSplitter("/hello/{msg}").getLength()).isEqualTo(8);
        assertThat(new URITemplateSplitter("/hello/{message}").getLength()).isEqualTo(8);
        assertThat(new URITemplateSplitter("/hello/{world}/{message}").getLength()).isEqualTo(10);
    }
}
