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

public interface MetadataProvider {

    // TODO Rewrite this using a record Metadata, instead xN! That's more efficient, and can cache.

    /**
     * Logical IRI of the resource at the physical IRI argument. This could be the same, or not;
     * e.g. a http: URL argument may encounter a HTTP redirect, and then this would return the new
     * final URL, or for a file: URL argument it may return what was read from e.g. an "$id"
     * attribute of a JSON-LD in that file, or something like that.
     */
    String getID(String iri);

    /**
     * Short human-readable 🏷️ label, as text; e.g. TITLE of HTML page found at the IRI.
     *
     * <p>Always returns text (never empty), but may fallback to e.g. last part of IRI.
     */
    String getLabel(String iri);

    /**
     * Longer human-readable 📜 description, as HTML.
     *
     * <p>E.g. first paragraph or &lt; meta ... description &gt; of a HTML page.
     *
     * <p>Always returns something (never null), but may be empty String.
     */
    String getDescriptionHTML(String iri);

    /**
     * HTML of an image for the IRI, e.g. <img...> with URL of a favicon. Or an 😃 Emoji!
     *
     * <p>Always returns something (never null), but may be empty String.
     */
    String getImageHTML(String iri);
}
