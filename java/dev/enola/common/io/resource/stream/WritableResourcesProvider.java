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
package dev.enola.common.io.resource.stream;

import dev.enola.common.io.resource.FileDescriptorResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.WritableResource;

import java.net.URI;

public class WritableResourcesProvider {

    private final ResourceProvider rp;

    public WritableResourcesProvider(ResourceProvider rp) {
        this.rp = rp;
    }

    public WritableResource getWritableResource(URI base, URI uri) {
        if (FileDescriptorResource.STDOUT_URI.equals(base)) return rp.getWritableResource(base);
        if (!base.toString().endsWith("/")) return rp.getWritableResource(base);
        return rp.getWritableResource(base.resolve(uri.getPath().substring(1)));
    }

    // TODO Extend and use this also in DocGen and other CommandWithModelAndOutput
}
