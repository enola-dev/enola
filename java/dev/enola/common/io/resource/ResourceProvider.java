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

import dev.enola.data.ProviderFromIRI;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * Resource Provider.
 *
 * <p>This is the primary interface to use (and e.g. @Inject) in clients of the <i>Resource
 * Framework</i>.
 *
 * @see dev.enola.common.io.resource
 */
public interface ResourceProvider extends ProviderFromIRI<Resource> {

    // TODO Replace @Nullable with get() and optional() pattern
    //   (as used e.g. in ChatLanguageModelProvider) and rename methods accordingly...

    // TODO Add (strongly typed) "metadata headers" (instead of e.g. only MediaType)

    default Resource getNonNull(URI uri) throws IOException {
        var resource = getResource(uri);
        if (resource == null) throw new IOException("Not found: " + uri);
        return resource;
    }

    default Optional<Resource> optional(URI uri) {
        try {
            return Optional.ofNullable(getResource(uri));
        } catch (Exception e) {
            ResourceProviders.LOG.info("Exception for {}", uri, e);
            return Optional.empty();
        }
    }

    @Override
    default @Nullable Resource get(String uri) {
        return getResource(URI.create(uri));
    }

    // TODO Rename getResource() to get() for consistency with the previous method
    @Nullable Resource getResource(URI uri);

    default @Nullable ReadableResource getReadableResource(URI uri) {
        return getResource(uri);
    }

    default @Nullable ReadableResource getReadableResource(String uri) {
        return get(uri);
    }

    // TODO getWritableResource() not @Nullable, but throws UnregisteredURISchemeException
    default @Nullable WritableResource getWritableResource(URI uri) {
        return getResource(uri);
    }

    default @Nullable WritableResource getWritableResource(String uri) {
        return get(uri);
    }
}
