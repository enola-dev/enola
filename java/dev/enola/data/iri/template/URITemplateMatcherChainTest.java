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

import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;

public class URITemplateMatcherChainTest {

    @Test
    @SuppressWarnings("unchecked") // TODO
    public void empty() throws Exception {
        var empty = URITemplateMatcherChain.builder().build();
        assertThat(empty.match("")).isEmpty();
        assertThat(empty.match("another/something")).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked") // TODO
    public void basic() throws Exception {
        var chainBuilder = URITemplateMatcherChain.builder();
        chainBuilder.add("thing/{name}", 1);
        chainBuilder.add("people/{firstName}-{lastName}/overview", 2);
        var chain = chainBuilder.build();

        assertThat(chain.listTemplates())
                .containsExactly("people/{firstName}-{lastName}/overview", "thing/{name}")
                .inOrder();

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
    @SuppressWarnings("unchecked") // TODO
    public void doNotMatchContained() throws Exception {
        var chain =
                URITemplateMatcherChain.builder().add("thing", 1).add("thing/{name}", 1).build();
        assertThat(chain.match("thingxoxo")).isEmpty();
        assertThat(chain.match("xoxothingxoxo")).isEmpty();
        assertThat(chain.match("xoxothing")).isEmpty();

        assertThat(chain.match("thingxoxo/hello")).isEmpty();
        assertThat(chain.match("xoxothingxoxo/hello")).isEmpty();
        assertThat(chain.match("xoxothing/hello")).isEmpty();
    }

    @Test
    public void matchLongest() throws Exception {
        var chain1 =
                URITemplateMatcherChain.<Integer>builder()
                        .add("aNS.anEntityKindName", 1)
                        .add("aNS.anEntityKindName/{foo}/{name}", 2)
                        // This is intentionally (just 1 character) SHORTER than the previous
                        .add("aNS.anEntityKindName/{x}/{y}/{z}", 3)
                        .build();
        checkMatchLongest(chain1);

        // Let's make sure this also works if the registration is in the other order
        var chain2 =
                URITemplateMatcherChain.<Integer>builder()
                        .add("aNS.anEntityKindName/{foo}/{name}", 2)
                        .add("aNS.anEntityKindName", 1)
                        .build();
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
