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

import java.io.File;
import java.net.URI;

public class LearnMimaTest {

    // TODO Rename LearnMimaTest to MimaTest

    // TODO Test other repo URLs than Maven Central (and make explicit)

    // TODO Allow explicit repo in get()

    // TODO class/record GAVR (with repoS), instead String

    // TODO Dependencies & Parent

    // TODO interface Artifact extends Thing

    @Test
    public void mariaDB4j() throws RepositoryException {
        try (var mima = new Mima()) {
            var gav = "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0";
            var response = mima.get(gav);
            var model = response.getEffectiveModel();
            assertThat(model).isNotNull();

            assertThat(Mima.xml(model))
                    .contains("<description>Java wrapper / launcher for MariaDB (or MySQL)");

            var allDependencies = mima.collect(gav);
            var classpath = Mima.classpath(allDependencies);
            var n = classpath.chars().filter(c -> c == File.pathSeparatorChar).count();
            assertThat(n).isEqualTo(19);

            // Origin:
            var central = URI.create("https://repo.maven.apache.org/maven2/");
            assertThat(Mima.origin(response)).hasValue(central);
            // Do it again, to see if it still works a 2nd time, even when it's already DL:
            response = mima.get(gav);
            assertThat(Mima.origin(response)).hasValue(central);
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
