/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.iri;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import static org.junit.Assert.assertThrows;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class IRITest {

    // TODO https://github.com/web-platform-tests/wpt/blob/master/url/resources/urltestdata.json
    // see  https://github.com/web-platform-tests/wpt/tree/master/url

    record TestIRI(
            boolean validIRI,
            boolean validURI,
            String text,
            String normalized,
            String scheme,
            String authority,
            String path,
            String query,
            String fragment) {}

    // TODO Instead of coding this out here, use (another) JSON like WHATWG urltestdata.json
    TestIRI[] tests =
            new TestIRI[] {
                new TestIRI(
                        true,
                        true,
                        "hTtPs://enola.dev/index.html?query#fragment",
                        "https://enola.dev/index.html?query#fragment",
                        "https",
                        "enola.dev",
                        "/index.html",
                        "query",
                        "fragment"),
                new TestIRI(
                        true,
                        true,
                        "hTtPs://enola.dev/index.html?query",
                        "https://enola.dev/index.html?query",
                        "https",
                        "enola.dev",
                        "/index.html",
                        "query",
                        ""),
                // TODO FIXME
                //                new TestIRI(
                //                        true,
                //                        true,
                //                        "hTtPs://enola.dev/index.html#fragment",
                //                        "https://enola.dev/index.html#fragment",
                //                        "https",
                //                        "enola.dev",
                //                        "/index.html",
                //                        "",
                //                        "fragment"),
                new TestIRI(
                        true,
                        false, // java.net.URI does not append trailing /
                        "https://enola.dev",
                        "https://enola.dev/",
                        "https",
                        "enola.dev",
                        "",
                        "",
                        ""),
                new TestIRI(
                        true,
                        true,
                        "schema:authority",
                        "schema:authority",
                        "schema",
                        "authority",
                        "",
                        "",
                        ""),
                new TestIRI(true, true, "schema:", "schema:", "schema", "", "", "", ""),
                new TestIRI(true, true, "relative", "relative", "", "", "relative", "", ""),
                new TestIRI(false, true, "?query", "", "", "", "", "query", ""),
                new TestIRI(true, true, "#fragment", "", "", "", "", "", "fragment"),
                new TestIRI(true, true, "", "", "", "", "", "", ""),
            };

    // TODO Test handling of + or %20 for space in path, query and fragment
    // TODO Test https://☃.net
    // TODO Test empty schemes: ":p", ":a/p", "://a", "://a/p",
    // TODO Test invalid schemes: ":p", ":a/p", "://a", "://a/p",
    // TODO Test whitespace, space and \t and CR/LF; at start, end and in the middle
    // TODO Test normalizing [0:0:0:0:0:0:0:1] to [::1]

    @Test
    public void empty() {} // TODO Remove once @Test iri() is no longer @Ignore

    @Test
    @Ignore // TODO
    public void iri() throws URISyntaxException, IRI.ValidationException {
        for (var test : tests) {
            var iri = IRI.parseUnencoded(test.text);
            check2(iri, test);

            var builder = IRI.builder();
            builder.scheme(test.scheme);
            builder.authority(test.authority);
            builder.path(test.path);
            builder.query(test.query);
            builder.fragment(test.fragment);
            check(builder.build(), test, test.normalized);
        }
    }

    void check2(IRI iri, TestIRI test) throws URISyntaxException, IRI.ValidationException {
        check(iri, test, test.text);

        var rebuiltIRI = iri.newBuilder().build();
        check(rebuiltIRI, test, test.normalized);
    }

    void check(IRI iri, TestIRI test, String string)
            throws URISyntaxException, IRI.ValidationException {
        assertThat(iri.toString()).isEqualTo(string);

        assertWithMessage(iri.toString()).that(iri.scheme()).isEqualTo(test.scheme());
        assertWithMessage(iri.toString()).that(iri.authority()).isEqualTo(test.authority());
        assertWithMessage(iri.toString()).that(iri.path()).isEqualTo(test.path());
        assertWithMessage(iri.toString()).that(iri.query()).isEqualTo(test.query());
        assertWithMessage(iri.toString()).that(iri.fragment()).isEqualTo(test.fragment());

        if (test.validIRI) iri.validate();
        else assertThrows(IRI.ValidationException.class, iri::validate);

        if (test.validURI) {
            assertWithMessage(iri.toString()).that(iri.toURI()).isEqualTo(new URI(test.text));
            assertWithMessage(iri.toString()).that(IRI.from(iri.toURI())).isEqualTo(iri);
        }
    }
}
