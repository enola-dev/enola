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

import com.google.common.base.Strings;
import com.google.common.io.ByteSink;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;

public class ResourceProviders implements ResourceProvider {

    // TODO Add support for ?charset=... to all resources
    // (see how it's done in FileDescriptorResource)

    // This is hard-coded to the ResourceProvider implementations
    // in this package, for now.  TODO Later, read ResourceProvider
    // implementations from the classpath via ServiceLoader, and
    // try each of them, based on a rank.
    ResourceProviderSPI testResourceProvider = new TestResource.Provider();

    @Override
    public Resource getResource(URI uri) {
        String scheme = uri.getScheme();
        if (Strings.isNullOrEmpty(scheme)) {
            throw new IllegalArgumentException("URI is missing a scheme: " + uri);
        } else if (scheme.startsWith("file")) {
            if (uri.getSchemeSpecificPart().startsWith("/")) {
                return new FileResource(Path.of(uri));
            } else {
                // This is for relative file URIs, like file:hello.txt
                return new FileResource(Path.of(uri.getSchemeSpecificPart(), ""));
            }
        } else if (scheme.startsWith(StringResource.SCHEME)) {
            return new ReadableButNotWritableResource(
                    new StringResource(uri.getSchemeSpecificPart()));
        } else if (scheme.startsWith(EmptyResource.SCHEME)) {
            return new ReadableButNotWritableResource(
                    new EmptyResource(uri.getSchemeSpecificPart()));
        } else if (scheme.startsWith(NullResource.SCHEME)) {
            return NullResource.INSTANCE;
        } else if (scheme.startsWith(ErrorResource.SCHEME)) {
            return ErrorResource.INSTANCE;
        } else if (scheme.startsWith("http")) {
            try {
                // TODO Replace UrlResource with alternative, when implemented
                return new ReadableButNotWritableResource(new UrlResource(uri.toURL()));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Malformed URI is not valid URL" + uri, e);
            }
        } else if (scheme.startsWith(ClasspathResource.SCHEME)) {
            return new ReadableButNotWritableResource(
                    new ClasspathResource(uri.getSchemeSpecificPart()));
        } else if (scheme.startsWith("fd")) {
            return new FileDescriptorResource(uri);
        } else if (scheme.startsWith(testResourceProvider.scheme())) {
            return testResourceProvider.getResource(uri);
        }
        throw new IllegalArgumentException("Unknown URI scheme '" + scheme + "' in: " + uri);
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
