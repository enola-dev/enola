/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import java.io.IOException;

/** {@link ReadableResource} which replaces text. */
public class ReplacingResource extends DelegatingReadableResource {

    // TODO Implement this more streaming-ly, without an intermediate String?

    public ReplacingResource(ReadableResource delegate, String... replacements) throws IOException {
        super(replace(delegate, replacements));
    }

    private static ReadableResource replace(ReadableResource delegate, String[] replacements)
            throws IOException {
        var template = delegate.charSource().read();
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Not enough replacements");
        }
        for (int i = 0; i < replacements.length; i += 2) {
            template = template.replace(replacements[i], replacements[i + 1]);
        }
        // NOT: return StringResource.of(template, delegate.mediaType());
        var replaced = new MemoryResource(delegate.mediaType());
        replaced.charSink().write(template);
        return replaced;
    }
}
