/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static java.util.Objects.requireNonNull;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class ResourceProviders implements ResourceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceProviders.class);

    private final Iterable<ResourceProvider> resourceProviders;

    public ResourceProviders(Iterable<ResourceProvider> resourceProviders) {
        this.resourceProviders = ImmutableList.copyOf(resourceProviders);
    }

    public ResourceProviders(ResourceProvider... resourceProviders) {
        this.resourceProviders = ImmutableList.copyOf(resourceProviders);
    }

    /**
     * Default constructor which enables all ResourceProviders.
     *
     * <p>This is ⚠️ NOT suitable e.g. for production in a server application, where you will want
     * more fine-grained control over allowed URI schemes to support in your application (if any at
     * all).
     */
    @Deprecated // TODO Replace all users with more explicit choices...
    // * FileResource is very useful in a CLI, but Very Bad Idea (TM)(R)(C) in a Server
    // * ClasspathResource is a *BAD* idea, by default; without filter!
    // * NullResource is probably not actually useful by default?
    // * ErrorResource is intended for testing, cannot not be active by default
    // * UrlResource Users may or may not want to allow external HTTP access!
    // * FileDescriptorResource should probably not be enable by default (notably e.g. in a Server)
    // * TestResource is intended for testing, cannot not be active by default
    public ResourceProviders() {
        this(
                new FileResource.Provider(),
                new ClasspathResource.Provider(),
                new StringResource.Provider(),
                new EmptyResource.Provider(),
                new NullResource.Provider(),
                new ErrorResource.Provider(),
                new OkHttpResource.Provider(),
                // For "jar:", use: new UrlResource.Provider(UrlResource.Scheme.jar),
                new FileDescriptorResource.Provider(),
                new TestResource.Provider());
    }

    @Override
    @NonNull
    public Resource getResource(URI uri) {
        String scheme = requireNonNull(uri, "uri").getScheme();
        if (Strings.isNullOrEmpty(scheme)) {
            throw new IllegalArgumentException("URI is missing a scheme: " + uri);
        }

        for (ResourceProvider resourceProvider : resourceProviders) {
            var resource = resourceProvider.getResource(uri);
            if (resource != null) return resource;
        }

        LOG.debug("Unknown URI scheme '" + scheme + "' in: {}", uri);
        return null;
    }
}
