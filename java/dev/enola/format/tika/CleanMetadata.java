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
package dev.enola.format.tika;

import com.google.common.collect.ImmutableMap;

import dev.enola.data.iri.IRI;
import dev.enola.thing.KIRI;

import org.jspecify.annotations.Nullable;

record CleanMetadata(@Nullable IRI iri, String... removeNames) {

    CleanMetadata() {
        this((IRI) null);
    }

    CleanMetadata(String iri, String... removeName) {
        this(IRI.from(iri), removeName);
    }

    // TODO Read this from a configuration file (in TTL) loaded into the Store

    static ImmutableMap<String, CleanMetadata> ALL =
            ImmutableMap.of(
                    "dc:description",
                    new CleanMetadata(KIRI.DC.DESCRIPTION, "description"),
                    "dc:creator",
                    new CleanMetadata(KIRI.DC.CREATOR, "author"),
                    "Content-Language",
                    new CleanMetadata(KIRI.DC.LANGUAGE),
                    "Content-Type",
                    new CleanMetadata(KIRI.E.MEDIA_TYPE),
                    "Content-Encoding",
                    new CleanMetadata(),
                    "viewport",
                    new CleanMetadata(),
                    "generator",
                    new CleanMetadata("https://enola.dev/html/generator"),
                    "Content-Type-Parser-Override", // Due to flaky ENV dep TikaThingConverterTest
                    new CleanMetadata());
}
