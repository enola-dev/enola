/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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

import dev.enola.common.context.TLC;
import dev.enola.data.ProviderFromIRI;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Resource Provider.
 *
 * <p>This is the primary interface to use (and e.g. @Inject) in clients of the <i>Resource
 * Framework</i>.
 *
 * @see dev.enola.common.io.resource
 */
public interface ResourceProvider extends ProviderFromIRI<Resource> {

    // TODO Rename all parameters from iri or uri to url - because that's what these are!

    // TODO Change all @Nullable Resource to Optional<Resource>... or, better, throw exception for
    // unknown schema

    // TODO Should this have a Resource getResource(URI uri, MediaType mediaType) ?

    // TODO Get rid of this again, just inject it into all users
    ResourceProvider CTX = uri -> TLC.get(ResourceProvider.class).getResource(uri);

    @Override
    default @Nullable Resource get(String iri) {
        return getResource(URI.create(iri));
    }

    // TODO Rename getResource to get for consistency
    @Nullable Resource getResource(URI uri);

    default @Nullable ReadableResource getReadableResource(URI uri) {
        return getResource(uri);
    }

    default @Nullable ReadableResource getReadableResource(String iri) {
        return get(iri);
    }

    // TODO getWritableResource() not @Nullable, but throws UnregisteredURISchemeException
    default @Nullable WritableResource getWritableResource(URI uri) {
        return getResource(uri);
    }

    default @Nullable WritableResource getWritableResource(String iri) {
        return get(iri);
    }

    // -------------------------------------------

    default @Nullable ReadableResource getReadableResource(URL url) {
        try {
            return getReadableResource(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL cannot be converted to URI: " + url, e);
        }
    }

    default @Nullable WritableResource getWritableResource(URL url) {
        try {
            return getWritableResource(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL cannot be converted to URI: " + url, e);
        }
    }

    default @Nullable Resource getResource(URL url) {
        try {
            return getResource(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL cannot be converted to URI: " + url, e);
        }
    }
}
