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
package dev.enola.common.io.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;

public abstract class DelegatingMultipartResource extends ReadableButNotWritableDelegatingResource
        implements MultipartResource {

    private final ImmutableMap<String, Resource> parts;

    protected DelegatingMultipartResource(
            ReadableResource baseResource, ImmutableMap<String, Resource> parts)
            throws IOException {
        super(baseResource);
        this.parts = parts;
    }

    @Override
    public ImmutableSet<String> parts() {
        return parts.keySet();
    }

    @Override
    public Resource part(String name) {
        var r = parts.get(name);
        if (r == null)
            throw new IllegalArgumentException(
                    "Only " + parts().toString() + ", invalid part: " + name);
        else return r;
    }
}
