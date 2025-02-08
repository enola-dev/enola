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

import org.junit.Test;

public class GAVRTest {

    // TODO Remove the "Gradle" terminology references entirely again (just Artifact)

    @Test(expected = IllegalArgumentException.class)
    public void parseGradleWithoutVersion() {
        GAVR.parseGradle("ch.vorburger.mariaDB4j:mariaDB4j-core");
    }

    @Test
    public void toArtifactString() {
        checkFromAndToGradle("ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0");
        checkFromAndToGradle(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:jar:3.1.0",
                "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0");
        checkFromAndToGradle("ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0:jar:javadoc");
        checkFromAndToGradle("ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0:zip:dist");
    }

    @Test
    public void parseGradleAndPkgURL() {
        check(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0",
                "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0");
        check(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:pom:3.1.0",
                "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0?type=pom");
        check(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:jar:javadoc:3.1.0",
                "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0?classifier=javadoc");
    }

    private void check(String gradleGAV, String purl) {
        checkFromAndToGradle(gradleGAV);
        checkFromAndToPkgURL(purl);

        var gavrFromGradle = GAVR.parseGradle(gradleGAV);
        assertThat(gavrFromGradle.toPkgURL()).isEqualTo(purl);

        var gavrFromPkgURL = GAVR.parsePkgURL(purl);
        assertThat(gavrFromPkgURL.toGradle()).isEqualTo(gradleGAV);
    }

    private void checkFromAndToGradle(String gav) {
        checkFromAndToGradle(gav, gav);
    }

    private void checkFromAndToGradle(String inputGAV, String expectedGAV) {
        var gavr = GAVR.parseGradle(inputGAV);
        assertThat(gavr.toGradle()).isEqualTo(expectedGAV);
    }

    private void checkFromAndToPkgURL(String purl) {
        var gavr = GAVR.parsePkgURL(purl);
        assertThat(gavr.toPkgURL()).isEqualTo(purl);
    }

    @Test
    public void parsePkgURL() {}
}
