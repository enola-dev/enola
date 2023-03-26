/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import com.google.common.base.Strings;
import com.google.common.io.ByteSink;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;

public class ResourceProviders implements ResourceProvider {

    // TODO ResourceProvidersTest coverage missing!

    // This is hard-coded to the ResourceProvider implementations
    // in this package, for now.  TODO Later, read ResourceProvider
    // implementations from the classpath via ServiceLoader, and
    // try each of them, based on a rank.

    @Override
    public ReadableResource getReadableResource(URI uri) {
        return getResource(uri);
    }

    @Override
    public WritableResource getWritableResource(URI uri) {
        return getResource(uri);
    }

    @Override
    public Resource getResource(URI uri) {
        if (Strings.isNullOrEmpty(uri.getScheme())) {
            throw new IllegalArgumentException("URI is missing a scheme: " + uri);
        } else if (uri.getScheme().startsWith("file")) {
            return new FileResource(Path.of(uri));
        } else if (uri.getScheme().startsWith(StringResource.SCHEME)) {
            return new ReadableButNotWritableResource(
                    new StringResource(uri.getSchemeSpecificPart()));
        } else if (uri.getScheme().startsWith(EmptyResource.SCHEME)) {
            return new ReadableButNotWritableResource(new EmptyResource());
        } else if (uri.getScheme().startsWith(NullResource.SCHEME)) {
            return NullResource.INSTANCE;
        } else if (uri.getScheme().startsWith(ErrorResource.SCHEME)) {
            return ErrorResource.INSTANCE;
        } else if (uri.getScheme().startsWith("http")) {
            try {
                // TODO Replace UrlResource with alternative, when implemented
                return new ReadableButNotWritableResource(new UrlResource(uri.toURL()));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Malformed URI is not valid URL" + uri, e);
            }
        }
        throw new IllegalArgumentException(
                "Unknown URI scheme '" + uri.getScheme() + "' in: " + uri);
    }

    private static class ReadableButNotWritableResource extends DelegatingReadableResource
            implements Resource {

        ReadableButNotWritableResource(ReadableResource resource) {
            super(resource);
        }

        @Override
        public ByteSink byteSink() {
            throw new UnsupportedOperationException(
                    "This is a read-only resource which is not writable.");
        }
    }
}
