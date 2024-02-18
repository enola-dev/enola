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
package dev.enola.rdf;

import com.google.common.base.Charsets;
import com.google.common.net.MediaType;

import org.eclipse.rdf4j.rio.RDFFormat;

public class RdfMediaType {
    // TODO implements MediaTypeProvider - when that's more pluggable

    public static final MediaType TURTLE =
            MediaType.parse(RDFFormat.TURTLE.getDefaultMIMEType()).withCharset(Charsets.UTF_8);

    public static final MediaType JSON_LD =
            MediaType.parse(RDFFormat.JSONLD.getDefaultMIMEType()).withCharset(Charsets.UTF_8);
}
