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
package dev.enola.common.diff.testlib;

import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.Fact;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

import org.jspecify.annotations.Nullable;

public class DiffingStringSubject extends Subject {

    public static DiffingStringSubject assertThat(@Nullable String actual) {
        return assertAbout(diffStrings()).that(actual);
    }

    private static Factory<DiffingStringSubject, String> diffStrings() {
        return DiffingStringSubject::new;
    }

    private final @Nullable String actual;

    public DiffingStringSubject(FailureMetadata metadata, @Nullable String actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void isEqualTo(String expected) {
        if (actual == null) assertThat(expected).isNull();
        if (actual.equals(expected)) return;

        failWithoutActual(Fact.fact("value", Differ.deltas(actual, expected)));
    }
}
