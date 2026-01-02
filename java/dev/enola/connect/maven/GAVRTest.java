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

import org.junit.Test;

public class GAVRTest {

    @Test(expected = IllegalArgumentException.class)
    public void parseGavWithoutVersion() {
        GAVR.parseGAV("ch.vorburger.mariaDB4j:mariaDB4j-core");
    }

    @Test
    public void toArtifactString() {
        checkFromAndToGAV("ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0");
        checkFromAndToGAV(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:jar:3.1.0",
                "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0");
        checkFromAndToGAV("ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0:jar:javadoc");
        checkFromAndToGAV("ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0:zip:dist");
    }

    @Test
    public void parseGavAndPkg() {
        check(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0",
                "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0");
        check(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:pom:3.1.0",
                "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0?type=pom");
        check(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:jar:javadoc:3.1.0",
                "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0?classifier=javadoc");
        check(
                "ch.vorburger.mariaDB4j:mariaDB4j-core:zip:dist:3.1.0",
                "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0?classifier=dist&type=zip");
    }

    @Test
    public void parsePkg() {
        var purl =
                "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0?repository_url=https%3A%2F%2Fmaven.google.com&type=pom";
        var gavr = GAVR.parsePkgURL(purl);
        assertThat(gavr.repo()).isEqualTo("https://maven.google.com");
        assertThat(gavr.toGAV()).isEqualTo("ch.vorburger.mariaDB4j:mariaDB4j-core:pom:3.1.0");
    }

    private void check(String gav, String purl) {
        checkFromAndToGAV(gav);
        checkFromAndToPkgURL(purl);

        var gavr = GAVR.parseGAV(gav);
        assertThat(gavr.toPkgURL()).isEqualTo(purl);

        var gavrFromPkgURL = GAVR.parsePkgURL(purl);
        assertThat(gavrFromPkgURL.toGAV()).isEqualTo(gav);
    }

    private void checkFromAndToGAV(String gav) {
        checkFromAndToGAV(gav, gav);
    }

    private void checkFromAndToGAV(String inputGAV, String expectedGAV) {
        var gavr = GAVR.parseGAV(inputGAV);
        assertThat(gavr.toGAV()).isEqualTo(expectedGAV);
    }

    private void checkFromAndToPkgURL(String purl) {
        var gavr = GAVR.parsePkgURL(purl);
        assertThat(gavr.toPkgURL()).isEqualTo(purl);
    }

    @Test
    public void parsePkgURL() {}
}
