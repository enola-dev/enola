/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import static org.junit.Assert.assertThrows;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class URLTest {

    record TestURL(
            boolean validIRI,
            boolean validURI,
            String text,
            String normalized,
            String scheme,
            String authority,
            String path,
            String query,
            String fragment) {}

    // TODO Use e.g. a CSV (with dev.enola.common.io.object.csv.CsvReader) instead of coding these
    // out here?
    TestURL[] tests =
            new TestURL[] {
                new TestURL(
                        true,
                        true,
                        "https://enola.dev",
                        "https://enola.dev/",
                        "https",
                        "enola.dev",
                        "",
                        "",
                        "")
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
    public void iri() throws URISyntaxException, URL.ValidationException {
        for (var test : tests) {
            var iri = URL.parseUnencoded(test.text);
            check2(iri, test);

            var builder = URL.builder();
            builder.scheme(test.scheme);
            builder.authority(test.authority);
            builder.path(test.path);
            builder.query(test.query);
            builder.fragment(test.fragment);
            check2(builder.build(), test);
        }
    }

    void check2(URL iri, TestURL test) throws URISyntaxException, URL.ValidationException {
        check(iri, test);

        var rebuiltIRI = iri.newBuilder().build();
        check(rebuiltIRI, test);
    }

    void check(URL iri, TestURL test) throws URISyntaxException, URL.ValidationException {
        assertThat(iri.toString()).isEqualTo(test.text);

        assertThat(iri.scheme()).isEqualTo(test.scheme());
        assertThat(iri.authority()).isEqualTo(test.authority());
        assertThat(iri.path()).isEqualTo(test.path());
        assertThat(iri.query()).isEqualTo(test.query());
        assertThat(iri.fragment()).isEqualTo(test.fragment());

        if (test.validIRI) iri.validate();
        else assertThrows(URL.ValidationException.class, iri::validate);

        if (test.validURI) {
            assertThat(iri.toURI()).isEqualTo(new URI(test.text));
            assertThat(URL.from(iri.toURI())).isEqualTo(iri);
        }
    }
}
