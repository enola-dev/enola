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
package dev.enola.thing.gen;

/**
 * KnownDocsProvider offers, if available, a "better URL" to use as link target in generated HTML or
 * Markdown etc. documentation for some known IRIs. For example:
 *
 * <ul>
 *   <li>https://docs.enola.dev/models/enola.dev/emoji/ instead of https://enola.dev/emoji (from the
 *       early days, before the HTTP redirector on enola.dev was set up)
 *   <li>https://docs.enola.dev/models/www.w3.org/1999/02/22-rdf-syntax-ns/type/ instead of
 *       http://www.w3.org/1999/02/22-rdf-syntax-ns#type (for which we cannot set up any HTTP
 *       redirector; and which is nicer to read than e.g. https://www.w3.org/TR/rdf-schema/#ch_type)
 * </ul>
 */
public interface KnownDocsProvider extends LinkTransformer {}
