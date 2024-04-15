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

import dev.enola.common.io.MoreFileSystems;

import java.net.MalformedURLException;
import java.net.URI;

public class ResourceProviders implements ResourceProvider {

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
        } else if (MoreFileSystems.URI_SCHEMAS.contains(scheme)) {
            return new FileResource(uri);
        } else if (scheme.startsWith(ClasspathResource.SCHEME)) {
            return new ReadableButNotWritableDelegatingResource(
                    new ClasspathResource(URIs.getPath(uri)));
        } else if (scheme.startsWith(StringResource.SCHEME)) {
            // NOT new StringResource(uriPath, mediaType),
            // because that is confusing, as it will chop off after # and interpret '?'
            // which is confusing for users, for this URI scheme. If "literal" resources
            // WITH MediaType are required, consider adding DataResource for data:
            return StringResource.of(uri.getSchemeSpecificPart());
        } else if (scheme.startsWith(EmptyResource.SCHEME)) {
            return new EmptyResource(uri);
        } else if (scheme.startsWith(NullResource.SCHEME)) {
            return NullResource.INSTANCE;
        } else if (scheme.startsWith(ErrorResource.SCHEME)) {
            return ErrorResource.INSTANCE;
        } else if (scheme.startsWith("http")) {
            try {
                // TODO Replace UrlResource with alternative, when implemented
                return new ReadableButNotWritableDelegatingResource(new UrlResource(uri.toURL()));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Malformed URI is not valid URL" + uri, e);
            }
        } else if (scheme.startsWith("fd")) {
            return new FileDescriptorResource(uri);
        } else if (scheme.startsWith(testResourceProvider.scheme())) {
            return testResourceProvider.getResource(uri);
        }
        throw new IllegalArgumentException("Unknown URI scheme '" + scheme + "' in: " + uri);
    }
}
