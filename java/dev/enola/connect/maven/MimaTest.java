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
package dev.enola.connect.maven;

import static com.google.common.truth.Truth.assertThat;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.List;

public class MimaTest {

    // TODO Allow explicit repo in get(), see https://github.com/maveniverse/mima/issues/166

    // TODO interface Artifact extends Thing, set Dependencies & Parent etc.

    // TODO Improve test coverage with a local repo server - is that worth it?!

    @Test
    public void mariaDB4j() throws RepositoryException {
        try (var mima = new Mima()) {
            var gav = GAVR.parseGAV("ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0");
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

    @Test
    public void jitpack() throws RepositoryException {
        var gav = GAVR.parseGAV("com.github.vorburger:java-multihash:ed14893c86");
        try (var mima = new Mima(List.of(Mima.JITPACK))) {
            assertThat(mima.get(gav)).isNotNull();
        }
        // TODO When https://github.com/maveniverse/mima/issues/166 is implemented:
        // try (var mima = new Mima()) {
        //    assertThat(mima.get(Mima.JITPACK, gav)).isNotNull();
        // }
    }

    @Test(expected = ArtifactResolutionException.class)
    public void nonExistingVersion() throws RepositoryException {
        try (var mima = new Mima()) {
            mima.get(GAVR.parseGAV("ch.vorburger.mariaDB4j:mariaDB4j-core:1.0.0"));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void gavWithoutVersion() throws RepositoryException {
        try (var mima = new Mima()) {
            mima.get(GAVR.parseGAV("ch.vorburger.mariaDB4j:mariaDB4j-core"));
        }
    }
}
