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
public interface ResourceProvider {

    Resource getResource(URI uri);

    default ReadableResource getReadableResource(URI uri) {
        return getResource(uri);
    }

    default WritableResource getWritableResource(URI uri) {
        return getResource(uri);
    }

    // -------------------------------------------

    default ReadableResource getReadableResource(URL url) {
        try {
            return getReadableResource(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL cannot be converted to URI: " + url, e);
        }
    }

    default WritableResource getWritableResource(URL url) {
        try {
            return getWritableResource(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL cannot be converted to URI: " + url, e);
        }
    }

    default Resource getResource(URL url) {
        try {
            return getResource(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL cannot be converted to URI: " + url, e);
        }
    }
}
