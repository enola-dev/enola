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
package dev.enola.thing.java;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.java.test.TestSomething;

import org.junit.Test;

public class TypeToBuilderTest {

    // TODO Write an APT processor that generates
    // //test/META-INF/dev.enola/https_--example.org-TestSomething

    @Test
    public void testSomethingBuilder() {
        var pair = TypeToBuilder.typeToBuilder(TestSomething.CLASS_IRI);
        assertThat(pair.builderClass()).isEqualTo(TestSomething.Builder.class);
        assertThat(pair.thingClass()).isEqualTo(TestSomething.class);
    }
}
