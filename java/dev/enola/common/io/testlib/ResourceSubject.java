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
package dev.enola.common.io.testlib;

import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.yamljson.JSON;

import java.io.IOException;

public final class ResourceSubject extends Subject {

    public static ResourceSubject assertThat(ReadableResource actual) {
        return assertAbout(resources()).that(actual);
    }

    public static Factory<ResourceSubject, ReadableResource> resources() {
        return ResourceSubject::new;
    }

    private final ReadableResource actual;

    public ResourceSubject(FailureMetadata metadata, ReadableResource actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void hasCharsEqualTo(ReadableResource resource) throws IOException {
        check(resource.toString())
                .that(actual.charSource().read())
                .isEqualTo(resource.charSource().read());
    }

    public void containsCharsOf(ReadableResource resource) throws IOException {
        var expected = resource.charSource().read();
        if (expected.isBlank()) throw new IllegalArgumentException(resource + " is blank");
        check(resource.toString()).that(actual.charSource().read()).contains(expected);
    }

    public void containsCharsOfIgnoreEOL(ReadableResource resource) throws IOException {
        var expected = resource.charSource().read();
        if (expected.isBlank()) throw new IllegalArgumentException(resource + " is blank");
        check(resource.toString())
                .that(trimLineEndWhitespace(actual.charSource().read()))
                .contains(expected);
    }

    public void hasJSONEqualTo(ReadableResource resource) throws IOException {
        check(resource.toString())
                .that(JSON.canonicalize(actual.charSource().read(), true))
                .isEqualTo(JSON.canonicalize(resource.charSource().read(), true));
    }

    // TODO other checks?

    private String trimLineEndWhitespace(String string) {
        return string.replaceAll("(?m) +$", "");
    }
}
