/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource;

/**
 * Resource.
 *
 * <p>Resource represent "the bytes that are at the {@link #uri()} - with their {@link
 * #mediaType()}, and a way to read from / write to them via {@link #byteSource()} and {@link
 * #byteSink()}.
 *
 * <p>PS: Nota bene that this <i>Resource</i> (of Enola's Java API) is <b>NOT</b> the same as the <a
 * href="https://docs.enola.dev/models/www.w3.org/2000/01/rdf-schema/Resource/"><code>rdfs:Resource
 * </code></a>, which is a Thing in Enola's Java API.
 */
public interface Resource extends ReadableResource, WritableResource {}
