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

/**
 * Metadata of an IRI, provided by {@link MetadataProvider}.
 *
 * @param imageHTML HTML of an image for the IRI, e.g. <img...> with URL of a favicon or something
 *     like that; or an 😃 Emoji!
 *     <p>Always returns something (never null), but may be empty String.
 * @param label Short human-readable 🏷️ label, as text; e.g. TITLE of HTML page found at the IRI.
 *     <p>Always returns text (never empty), but may fallback to e.g. last part of IRI.
 * @param descriptionHTML Longer human-readable 📜 description, as HTML.
 *     <p>E.g. first paragraph or &lt; meta ... description &gt; of a HTML page.
 *     <p>Always returns something (never null), but may be empty String.
 */
public record Metadata(String imageHTML, String label, String descriptionHTML) {}
