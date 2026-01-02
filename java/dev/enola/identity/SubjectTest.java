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
package dev.enola.identity;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.TLC;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;

import org.junit.Test;

public class SubjectTest {

    @Test
    public void ctx() {
        var tbf = new ProxyTBF(ImmutableThing.FACTORY);
        var sb = tbf.create(Subject.Builder.class, Subject.class);

        var subject = sb.iri("https://example.com/alice").label("Alice").build();
        assertThat(subject.iri()).isEqualTo("https://example.com/alice");

        try (var ctx = TLC.open().push(SubjectContextKey.USER, subject)) {
            assertThat(ctx.get(SubjectContextKey.USER)).isEqualTo(subject);
        }
    }
}
