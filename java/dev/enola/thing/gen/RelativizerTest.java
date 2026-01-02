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
package dev.enola.thing.gen;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.net.URI;

public class RelativizerTest {

    @Test
    public void relativize() {
        assertThat(
                        Relativizer.relativize(
                                URI.create("file:/docs/models/enola.dev/yaml/shorthand.md"),
                                URI.create("file:///docs/models/enola.dev/properties.ttl")))
                .isEqualTo("../properties.ttl");
    }

    @Test
    public void relativizeWithQuery() {
        assertThat(
                        Relativizer.relativize(
                                URI.create("file:/docs/models/enola.dev/yaml/shorthand.md"),
                                URI.create(
                                        "file:///docs/models/enola.dev/properties.ttl?query=arg")))
                .isEqualTo("../properties.ttl?query=arg");
    }

    @Test
    public void relativizeSame() {
        assertThat(
                        Relativizer.relativize(
                                URI.create("file:///foo/bar.md"), URI.create("file:///foo/bar.md")))
                .isEqualTo("#");
    }

    @Test
    public void relativizeCannot() {
        assertThat(
                        Relativizer.relativize(
                                URI.create("file:///foo/bar.md"),
                                URI.create("jar:file:/some/models.jar!/enola.dev/properties.ttl")))
                .isEqualTo("jar:file:/some/models.jar!/enola.dev/properties.ttl");
    }

    @Test
    public void dropSchemeAddExtension() {
        check("https://enola.dev/ett", "enola.dev/ett.md");
        check("https://enola.dev/some/thing", "enola.dev/some/thing.md");
        check("https://enola.dev", "enola.dev.md");
        check("https://x", "x.md");

        check(
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                "www.w3.org/1999/02/22-rdf-syntax-ns/type.md");
        check("http://www.w3.org/2000/01/rdf-schema#", "www.w3.org/2000/01/rdf-schema.md");
        check("http://www.w3.org/2000/01/rdf-schema", "www.w3.org/2000/01/rdf-schema.md");

        check(
                "https://example.org/greetingNUMBER?NUMBER=42",
                "example.org/greetingNUMBER.md?NUMBER=42");
        // TODO check("https://example.org/greetingNUMBER?NUMBER=42#frag",
        // "example.org/greetingNUMBER.md?NUMBER=42#frag");

        check("fs:localhost", "fs/localhost.md");

        check("file:/tmp/astronomy.ttl", "file/tmp/astronomy.ttl.md");
        check("file:///tmp/astronomy.ttl", "file/tmp/astronomy.ttl.md");
        check("file://hostname/tmp/astronomy.ttl", "file/hostname/tmp/astronomy.ttl.md");

        // TODO The following are technically illegal and unexpected, of course; but just in case:
        // check("http:/enola.dev/ett", "enola.dev/ett.md");
        // check("http:enola.dev/ett", "enola.dev/ett.md");
        // check("http:/x", "x.md");
        // check("http:x", "x.md");
    }

    private void check(String absThingIRI, String relIRI) {
        assertThat(Relativizer.dropSchemeAddExtension(URI.create(absThingIRI), "md"))
                .isEqualTo(URI.create(relIRI));
    }
}
