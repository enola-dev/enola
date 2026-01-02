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
package dev.enola.common.io.hashbrown;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import static org.junit.Assert.assertThrows;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.*;

import org.junit.Rule;
import org.junit.Test;

public class IntegrityValidatingDelegatingResourceTest {

    ResourceProvider rp =
            new IntegrityValidatingDelegatingResource.Provider(new ClasspathResource.Provider());

    public @Rule SingletonRule r1 = $(MediaTypeProviders.set());

    @Test
    public void example() {
        // This is useful "to see it", e.g. when adding new tests
        // throw new RuntimeException(
        //        Multihashes.example(Multihash.Type.sha2_512, Multibase.Base.Base58BTC));
    }

    @Test
    public void bad() {
        IntegrityViolationException thrown =
                assertThrows(
                        IntegrityViolationException.class,
                        () -> {
                            check(
                                    "z8VsnXyGnRwJpnrQXB8KcLstvgFYGZ2f5BCm3DVndcNZ8NswtkCqsut69e7yd1FKNtettjgy669GNVt8VSTGxkAiJaB");
                        });
        assertThat(thrown).hasMessageThat().contains("z8VsnXy"); // actual
        assertThat(thrown).hasMessageThat().contains("z8Vw9J6"); // expected
    }

    @Test
    public void good() {
        check(
                "z8Vw9J6ZbuvzUV7wuau1uws8hw2QTZUeFfgwdyre5LmC1yFUoR2b7WyR2M8CaDR9Z6A2FafkPjmETcLKetbBr5d2Qv7");
    }

    void check(String hash) {
        rp.get("classpath:/test.png?integrity=" + hash).byteSource();
    }
}
