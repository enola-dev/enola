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
package dev.enola.thing.gen;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.net.URI;

public class RelativizerTest {

    @Test
    public void relativize() {
        check("https://enola.dev/ett", "enola.dev/ett.md");
        check("https://enola.dev/some/thing", "enola.dev/some/thing.md");
        check("https://enola.dev", "enola.dev.md");
        check("https://x", "x.md");

        // The following are technically illegal and unexpected, of course; but just in case:
        check("http:/enola.dev/ett", "enola.dev/ett.md");
        check("http:enola.dev/ett", "enola.dev/ett.md");
        check("http:/x", "x.md");
        check("http:x", "x.md");
    }

    private void check(String absThingIRI, String relIRI) {
        assertThat(Relativizer.relativize(URI.create(absThingIRI), "md"))
                .isEqualTo(URI.create(relIRI));
    }
}