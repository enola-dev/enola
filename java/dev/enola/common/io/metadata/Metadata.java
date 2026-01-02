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
package dev.enola.common.io.metadata;

import static java.util.Objects.requireNonNull;

import com.google.errorprone.annotations.Immutable;

import java.util.Comparator;

/**
 * Metadata of an IRI, provided by {@link MetadataProvider}.
 *
 * @param iri IRI about which this is Metadata.
 * @param imageHTML HTML of an image for the IRI, e.g. &lt;img...&gt; with URL of a favicon or
 *     something like that; or an 😃 Emoji!
 *     <p>Always returns something (never null), but may be empty String.
 * @param imageURL URL (not HTML) of an image for the IRI, but never the 😃 Emoji.
 *     <p>Always returns something (never null), but may be empty String.
 * @param emoji An 😃 Emoji for the IRI, as String (not URL or HTML).
 *     <p>Always returns something (never null), but may be empty String.
 * @param curie An IRI converted to a "CURIE" (e.g. rdfs:Class), if available.
 *     <p>Always returns something (never null), but may be empty String if no suitable CURIE could
 *     be determined.
 * @param label Short human-readable 🏷️ label, as text; e.g. TITLE of HTML page found at the IRI.
 *     <p>Always returns text (never empty), but may fallback to e.g. last part of IRI.
 * @param descriptionHTML Longer human-readable 📜 description, as HTML.
 *     <p>E.g. first paragraph or &lt; meta ... description &gt; of a HTML page.
 *     <p>Always returns something (never null), but may be empty String.
 */
@Immutable
// TODO Metadata implements Thing!
public record Metadata(
        String iri,
        String imageHTML,
        String imageURL,
        String emoji,
        String curie,
        String label,
        String descriptionHTML) {

    public static final Comparator<Metadata> IRI_Comparator =
            (o1, o2) ->
                    Comparator.nullsFirst(Comparator.<String>naturalOrder())
                            .compare(o1.iri, o2.iri);

    public Metadata(
            String iri,
            String imageHTML,
            String imageURL,
            String emoji,
            String curie,
            String label,
            String descriptionHTML) {
        if (!iri.contains("{")) {
            if (iri.contains("[")) {
                throw new IllegalArgumentException(iri);
            }
            // JUST for temporary debugging: URI.create(iri);
        }
        this.iri = requireNonEmpty(iri, "iri", iri);
        this.imageHTML = requireNonNull(imageHTML, () -> "imageHTML of " + iri).trim();
        this.imageURL = requireNonNull(imageURL, () -> "imageURL of " + iri).trim();
        this.emoji = requireNonNull(emoji, () -> "emoji of " + iri).trim();
        this.curie = requireNonNull(curie, () -> "curie of " + iri);
        this.label = requireNonEmpty(label, "label", iri);
        this.descriptionHTML =
                requireNonNull(descriptionHTML, () -> "descriptionHTML of " + iri).trim();
    }

    private String requireNonEmpty(String string, String name, String context) {
        if (requireNonNull(string, name).trim().isEmpty())
            throw new IllegalArgumentException(name + " of " + context + " is empty?!");
        return string;
    }
}
