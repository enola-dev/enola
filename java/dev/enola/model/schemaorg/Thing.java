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
package dev.enola.model.schemaorg;

import dev.enola.model.enola.HasDescription;
import dev.enola.model.w3.rdfs.HasLabel;
import dev.enola.thing.HasIRI;
import dev.enola.thing.java.HasType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.net.URI;

@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public interface Thing extends HasIRI, HasType, HasLabel, HasDescription { // skipcq: JAVA-E0169
    // TODO , HasImage

    // https://schema.org/image
    @Nullable URI image();
}
