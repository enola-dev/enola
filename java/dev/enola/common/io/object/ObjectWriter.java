/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object;

import com.google.common.net.MediaType;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.WritableResource;

import java.io.IOException;
import java.util.Optional;

public interface ObjectWriter {

    @CanIgnoreReturnValue
    boolean write(Object instance, WritableResource resource) throws IOException;

    default Optional<String> write(Object instance, MediaType mediaType) throws IOException {
        var resource = new MemoryResource(mediaType);
        var success = write(instance, resource);

        if (success) return Optional.of(resource.charSource().read());
        else return Optional.empty();
    }
}
