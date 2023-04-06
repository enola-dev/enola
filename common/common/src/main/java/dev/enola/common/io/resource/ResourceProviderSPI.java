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

import java.net.URI;

/** Service Provider Interface, for implementations of {@link ResourceProvider}. */
public interface ResourceProviderSPI extends ResourceProvider {
    String scheme();

    @Override
    default Resource getResource(URI uri) {
        if (!uri.getScheme().equals(scheme())) {
            throw new IllegalArgumentException(
                    "This ResourceProviderSPI implementation does not support: " + uri);
        }
        return getResourceImplementation(uri);
    }

    Resource getResourceImplementation(URI uri);
}
