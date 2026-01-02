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
package dev.enola.common.io.resource.cache;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.Resource;
import dev.enola.common.io.resource.ResourceProvider;

import org.jspecify.annotations.Nullable;

import java.net.URI;

/**
 * OptionalCachingResourceProvider is a {@link AlwaysCachingResourceProvider} variant which only
 * caches resources if the request URI contains a ?cache=forever query.
 */
public class OptionalCachingResourceProvider extends AlwaysCachingResourceProvider {

    public static final String CACHE = "cache";

    public OptionalCachingResourceProvider(ResourceProvider delegate) {
        super(delegate);
    }

    @Override
    public @Nullable Resource getResource(URI uri) {
        if (URIs.hasQueryParameter(uri, CACHE)) return super.getResource(uri);
        else return delegate.getResource(uri);
    }
}
