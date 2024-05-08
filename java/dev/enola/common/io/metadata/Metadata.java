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
package dev.enola.common.io.metadata;

import static java.util.Objects.requireNonNull;

/**
 * Metadata of an IRI, provided by {@link MetadataProvider}.
 *
 * @param iri IRI about which this is Metadata.
 * @param imageHTML HTML of an image for the IRI, e.g. <img...> with URL of a favicon or something
 *     like that; or an 😃 Emoji!
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
// TODO Metadata implements Thing!
public record Metadata(
        String iri, String imageHTML, String curie, String label, String descriptionHTML) {

    public Metadata(
            String iri, String imageHTML, String curie, String label, String descriptionHTML) {
        this.iri = requireNonEmpty(iri, "iri");
        this.imageHTML = requireNonNull(imageHTML, "imageHTML").trim();
        this.curie = requireNonNull(curie, "curie");
        this.label = requireNonEmpty(label, "label");
        this.descriptionHTML = requireNonNull(descriptionHTML, "descriptionHTML").trim();
    }

    private String requireNonEmpty(String string, String name) {
        if (requireNonNull(string, name).trim().isEmpty())
            throw new IllegalArgumentException(name + " is empty?!");
        return string;
    }
}
