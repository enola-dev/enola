/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static com.google.common.truth.Truth8.assertThat;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;

public class URITemplateMatcherChainTest {

    @Test
    public void basic() throws Exception {
        var chain = new URITemplateMatcherChain<Integer>();
        assertThat(chain.match("")).isEmpty();
        assertThat(chain.match("another/something")).isEmpty();

        chain.add("thing/{name}", 1);
        chain.add("people/{firstName}-{lastName}/overview", 2);

        assertThat(chain.match("thing/")).isEmpty();
        assertThat(chain.match("thing/hello"))
                .hasValue(new SimpleEntry<>(1, ImmutableMap.of("name", "hello")));
        assertThat(chain.match("people/donald-duck/overview"))
                .hasValue(
                        new SimpleEntry<>(
                                2, ImmutableMap.of("firstName", "donald", "lastName", "duck")));

        assertThat(chain.match("")).isEmpty();
        assertThat(chain.match("another/something")).isEmpty();
    }

    @Test
    public void matchLongest() throws Exception {
        var chain1 = new URITemplateMatcherChain<Integer>();
        chain1.add("aNS.anEntityKindName", 1);
        chain1.add("aNS.anEntityKindName/{foo}/{name}", 2);
        // Tihs is intentionally (just 1 character) SHORTER than the previous
        chain1.add("aNS.anEntityKindName/{x}/{y}/{z}", 3);
        checkMatchLongest(chain1);

        // Let's make sure this also works if the registration is in the other order
        var chain2 = new URITemplateMatcherChain<Integer>();
        chain2.add("aNS.anEntityKindName/{foo}/{name}", 2);
        chain2.add("aNS.anEntityKindName", 1);
        checkMatchLongest(chain2);
    }

    private void checkMatchLongest(URITemplateMatcherChain<Integer> chain) {
        assertThat(chain.match("somethingelse")).isEmpty();
        assertThat(chain.match("aNS.anEntityKindName"))
                .hasValue(new SimpleEntry<>(1, ImmutableMap.of()));
        assertThat(chain.match("aNS.anEntityKindName/hello/world"))
                .hasValue(new SimpleEntry<>(2, ImmutableMap.of("foo", "hello", "name", "world")));
    }
}
