/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.secret;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class SecretManagersTest {

    SecretManager secretManager = new InMemorySecretManager();

    @Test
    public void empty() throws IOException {
        assertThat(secretManager.getOptional("foo")).isEmpty();
    }

    @Test
    public void store() throws IOException {
        var bar = "bar".toCharArray();
        secretManager.store("foo", bar);
        assertThat(bar).isEqualTo(new char[] {0, 0, 0});

        try (var secret = secretManager.get("foo")) {
            secret.process(it -> assertThat(it).isEqualTo("bar".toCharArray()));
        }

        // Intentionally similarly again, but now also test the clearing business
        var hold = new AtomicReference<char[]>();
        try (var secret = secretManager.get("foo")) {
            secret.process(
                    newValue -> {
                        hold.set(newValue);
                        assertThat(hold.get()).isEqualTo("bar".toCharArray());
                    });
            assertThat(hold.get()).isEqualTo(new char[] {0, 0, 0});
        }

        // Only if you *REALLY* must, e.g. because you need to pass it to an existing API, then:
        String azkaban;
        try (var secret = secretManager.get("foo")) {
            azkaban = secret.map(String::new);
        }
        assertThat(azkaban).isEqualTo("bar");
    }
}
