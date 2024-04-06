/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import dev.enola.common.CloseableIterator;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.data.ProviderFromIRI;

import java.nio.file.FileSystem;

/**
 * Resource Provider similar to the basic {@link ResourceProvider} - but for multiple resources,
 * collected using an IRI with a "glob" (e.g. à la Java's {@link FileSystem#getPathMatcher
 * getPathMatcher}).
 */
public interface GlobResourceProvider extends ProviderFromIRI<CloseableIterator<ReadableResource>> {

    @Override
    CloseableIterator<ReadableResource> get(String globIRI);
}
