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
package dev.enola.rdf;

import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.yamljson.JSON;

import java.io.IOException;

public final class ResourceSubject extends Subject {

    // TODO Move this class to a //common/testlib

    public static ResourceSubject assertThat(ReadableResource actual) {
        return assertAbout(resources()).that(actual);
    }

    public static Factory<ResourceSubject, ReadableResource> resources() {
        return ResourceSubject::new;
    }

    private ReadableResource actual;

    public ResourceSubject(FailureMetadata metadata, ReadableResource actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void hasCharsEqualTo(ReadableResource resource) throws IOException {
        check("charSource")
                .that(actual.charSource().read())
                .isEqualTo(resource.charSource().read());
    }

    public void containsCharsOf(ReadableResource resource) throws IOException {
        check("charSource").that(actual.charSource().read()).contains(resource.charSource().read());
    }

    public void hasJSONEqualTo(ReadableResource resource) throws IOException {
        check("charSourceAsJSON")
                .that(JSON.normalize(actual.charSource().read()))
                .isEqualTo(JSON.normalize(resource.charSource().read()));
    }

    // TODO other checks?
}
