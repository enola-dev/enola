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
package dev.enola.thing.gen.markdown;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.io.metadata.Metadata;
import dev.enola.thing.template.TemplateService;
import dev.enola.thing.template.Templates;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public class MarkdownLinkWriterTest {

    @Test
    public void writeMarkdownLink() throws IOException {
        var sb = new StringBuilder();
        new MarkdownLinkWriter(Templates.Format.Mustache)
                .writeMarkdownLink(
                        new Metadata(
                                "https://example.org/greeting42", "", "", "", "", "greeting42", ""),
                        sb,
                        URI.create("file:///out/greeting.md"),
                        URI.create("file:///out/"),
                        iri -> true,
                        TemplateService.NONE);
        assertThat(sb.toString()).isEqualTo("[greeting42](example.org/greeting42.md)");
    }

    @Test
    public void writeMarkdownLinkWithCurieAndSameLabel() throws IOException {
        var sb = new StringBuilder();
        new MarkdownLinkWriter(Templates.Format.Mustache)
                .writeMarkdownLink(
                        new Metadata(
                                "https://example.org/greeting42",
                                "",
                                "",
                                "",
                                "ex:greeting42",
                                "greeting42",
                                ""),
                        sb,
                        URI.create("file:///out/greeting.md"),
                        URI.create("file:///out/"),
                        iri -> true,
                        TemplateService.NONE);
        assertThat(sb.toString()).isEqualTo("[`ex:greeting42`](example.org/greeting42.md)");
    }

    @Test
    public void writeMarkdownLinkWithCurieAndBetterLabel() throws IOException {
        var sb = new StringBuilder();
        new MarkdownLinkWriter(Templates.Format.Mustache)
                .writeMarkdownLink(
                        new Metadata(
                                "https://example.org/greeting42",
                                "",
                                "",
                                "",
                                "ex:greeting42",
                                "Da Greeting! ;)",
                                ""),
                        sb,
                        URI.create("file:///out/greeting.md"),
                        URI.create("file:///out/"),
                        iri -> true,
                        TemplateService.NONE);
        assertThat(sb.toString())
                .isEqualTo("[`ex:greeting42` Da Greeting! ;)](example.org/greeting42.md)");
    }

    @Test
    public void unknownIRIs() throws IOException {
        var sb = new StringBuilder();
        new MarkdownLinkWriter(Templates.Format.Mustache)
                .writeMarkdownLink(
                        new Metadata(
                                "https://unknown.org/whatever", "", "", "", "", "whatever", ""),
                        sb,
                        URI.create("file:///out/greeting.md"),
                        URI.create("file:///out/"),
                        iri -> false, // sic!
                        TemplateService.NONE);
        assertThat(sb.toString()).isEqualTo("[whatever](https://unknown.org/whatever)");
    }

    @Test
    public void knownIRIs() throws IOException {
        var sb = new StringBuilder();
        new MarkdownLinkWriter(Templates.Format.Mustache)
                .writeMarkdownLink(
                        new Metadata("https://enola.dev/emoji", "", "", "", "", "emoji", ""),
                        sb,
                        URI.create("file:///out/greeting.md"),
                        URI.create("file:///out/"),
                        iri -> false, // sic!
                        TemplateService.NONE);
        assertThat(sb.toString())
                .isEqualTo("[emoji](https://docs.enola.dev/models/enola.dev/emoji/)");
    }

    @Test
    public void writeTemplateMarkdownLink() throws IOException {
        var sb = new StringBuilder();
        new MarkdownLinkWriter(Templates.Format.Mustache)
                .writeMarkdownLink(
                        new Metadata(
                                "https://example.org/greeting42", "", "", "", "", "greeting42", ""),
                        sb,
                        URI.create("file:///out/greeting.md"),
                        URI.create("file:///out/"),
                        iri -> true,
                        TEST_TEMPLATE_SERVICE);
        assertThat(sb.toString())
                .isEqualTo("[greeting42](example.org/greeting_NUMBER.md?NUMBER=42)");
    }

    private static final TemplateService TEST_TEMPLATE_SERVICE =
            nonTemplateIRI ->
                    Optional.of(
                            new TemplateService.Breakdown(
                                    "https://example.org/greeting{NUMBER}",
                                    ImmutableMap.of("NUMBER", "42")));
}
