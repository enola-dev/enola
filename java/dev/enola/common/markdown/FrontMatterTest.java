/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.markdown;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

class FrontMatterTest {

    @Test
    void emptyWhenNoFrontMatter() {
        var fm = FrontMatter.from("# Just a heading\n\nSome text.");
        assertThat(fm.title()).isNull();
        assertThat(fm.description()).isNull();
    }

    @Test
    void emptyWhenBlankOrNull() {
        assertThat(FrontMatter.from("")).isEqualTo(FrontMatter.EMPTY);
        assertThat(FrontMatter.from((String) null)).isEqualTo(FrontMatter.EMPTY);
    }

    @Test
    void frontMatterWithTitleAndDescription() {
        var md =
                """
                ---
                title:   My Clean Title
                description: First line of description
                  spanning across multiple lines.
                ---
                # Content
                """;
        var fm = FrontMatter.from(md);
        assertThat(fm.title()).isEqualTo("My Clean Title");
        assertThat(fm.description())
                .isEqualTo("First line of description spanning across multiple lines.");
    }

    @Test
    void ignoresUnknownPropertiesWithoutError() {
        var md =
                """
                ---
                type: Concept
                okf_version: 0.2
                tags:
                  - ai
                  - memory
                title: Ignored Other Fields
                description: Valid description
                ---
                # Heading
                """;
        var fm = FrontMatter.from(md);
        assertThat(fm.title()).isEqualTo("Ignored Other Fields");
        assertThat(fm.description()).isEqualTo("Valid description");
    }

    @Test
    void blankTitleAndDescriptionBecomeNull() {
        var md =
                """
                ---
                title: "   "
                description: ""
                ---
                # Heading
                """;
        var fm = FrontMatter.from(md);
        assertThat(fm.title()).isNull();
        assertThat(fm.description()).isNull();
    }

    @Test
    void malformedYamlFallsBackToEmpty() {
        var md =
                """
                ---
                [invalid yaml: { : :
                ---
                # Heading
                """;
        var fm = FrontMatter.from(md);
        assertThat(fm).isEqualTo(FrontMatter.EMPTY);
        assertThat(fm.title()).isNull();
        assertThat(fm.description()).isNull();
    }
}
