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
package dev.enola.rdf.io;

import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;

final class ModelSubject extends Subject {

    // TODO Move this class to a //common/testlib

    public static ModelSubject assertThat(Model actual) {
        return assertAbout(resources()).that(actual);
    }

    public static Factory<ModelSubject, Model> resources() {
        return ModelSubject::new;
    }

    private final Model actual;

    public ModelSubject(FailureMetadata metadata, Model actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void isEqualTo(Model expected) {
        if (!Models.isomorphic(actual, expected)) {
            // TODO Canonicalizer-like sorting of Statements by IRI
            Truth.assertThat(actual).isEqualTo(expected);
        }
    }
}
