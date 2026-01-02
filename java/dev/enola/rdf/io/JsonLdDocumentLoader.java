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
package dev.enola.rdf.io;

import static no.hasmac.jsonld.JsonLdErrorCode.LOADING_REMOTE_CONTEXT_FAILED;

import dev.enola.common.io.resource.Resource;
import dev.enola.common.io.resource.ResourceProvider;

import no.hasmac.jsonld.JsonLdError;
import no.hasmac.jsonld.document.Document;
import no.hasmac.jsonld.document.JsonDocument;
import no.hasmac.jsonld.http.media.MediaType;
import no.hasmac.jsonld.loader.DocumentLoaderOptions;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

class JsonLdDocumentLoader implements no.hasmac.jsonld.loader.DocumentLoader {

    private final ResourceProvider rp;

    public JsonLdDocumentLoader(ResourceProvider rp) {
        this.rp = rp;
    }

    @Override
    public Document loadDocument(URI uri, DocumentLoaderOptions options) throws JsonLdError {
        Resource resource = rp.getResource(uri);
        if (resource == null) throw new IllegalArgumentException("No Resource: " + uri);
        // TODO https://github.com/HASMAC-AS/hasmac-json-ld/issues/11 avoid toString() #efficient
        MediaType hasmacMediaType = MediaType.of(resource.mediaType().toString());
        try {
            Reader reader = resource.charSource().openStream();
            return JsonDocument.of(hasmacMediaType, reader);
        } catch (IOException e) {
            throw new JsonLdError(LOADING_REMOTE_CONTEXT_FAILED, e);
        }
    }
}
