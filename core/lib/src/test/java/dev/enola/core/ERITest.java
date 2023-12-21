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
package dev.enola.core;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.core.ERI.create;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class ERITest {

    // TODO Add IRI testing, so non-URI, with special characters

    @Test
    public void basic() {
        check("something");
        check("enola:something", "something");
        check("enola:enola.dev/type/abc", "enola.dev/type/abc");

        // URN https://en.wikipedia.org/wiki/Uniform_Resource_Name
        check("urn:enola:demo.foo/abc/def", "demo.foo/abc/def");

        // https://en.wikipedia.org/wiki/Percent-encoding
        check("something%3Aelse", "something:else");
        // TODO I would actually like to allow this, by parsing without using java.net.URI...
        nok("something:else");

        // Authority needs to be empty, but it's intended to be eventually supported, for federation
        nok("enola://demo.enola.dev/xyz");

        // Any "?query" and "#fragment" of the URI are not supported
        nok("a/b?q=xyz");
        nok("a/b#fragment");
        nok("a/b?q=xyz#fragment");

        // Non enola: schemas are not (currently) supported
        nok("https://www.google.com");
        nok("mailto:support@enola.dev");
    }

    private void check(String eri, String path) {
        assertThat(create(eri).getPath()).isEqualTo(path);
    }

    private void check(String eri) {
        check(eri, eri);
    }

    private void nok(String eri) {
        assertThrows(IllegalArgumentException.class, () -> create(eri));
    }
}
