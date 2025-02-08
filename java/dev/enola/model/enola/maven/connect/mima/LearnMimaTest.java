/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.model.enola.maven.connect.mima;

import static com.google.common.truth.Truth.assertThat;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.junit.Test;

public class LearnMimaTest {

    // TODO Origin repo

    // TODO XML

    // TODO Classpath

    // TODO Test other repo URLs than Maven Central (and make explicit)

    // TODO Allow explicit repo in get()

    // TODO class/record GAV, instead String

    // TODO Dependencies & Parent

    // TODO interface Artifact extends Thing

    @Test
    public void mariaDB4j() throws RepositoryException {
        try (var mima = new Mima()) {
            var model = mima.get("ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0").getEffectiveModel();
            assertThat(model).isNotNull();
        }
    }

    @Test(expected = ArtifactResolutionException.class)
    public void nonExistingVersion() throws RepositoryException {
        try (var mima = new Mima()) {
            mima.get("ch.vorburger.mariaDB4j:mariaDB4j-core:1.0.0");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void gavWithoutVersion() throws RepositoryException {
        try (var mima = new Mima()) {
            mima.get("ch.vorburger.mariaDB4j:mariaDB4j-core");
        }
    }
}
