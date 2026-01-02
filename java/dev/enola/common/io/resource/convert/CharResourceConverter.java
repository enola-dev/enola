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
package dev.enola.common.io.resource.convert;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.AbstractResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Copies all characters from the input into the output resource.
 *
 * <p>Depending on their respective {@link MediaType#charset()}, this may change the text encoding!
 */
public class CharResourceConverter implements CatchingResourceConverter {

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws IOException {
        // TODO Test if this couldn't just be written as: (and delete private helpers)
        // if (!from.mediaType().charset().equals(into.mediaType().charset()) {
        if (hasCharset(from) && hasCharset(into) && !charset(from).equals(charset(into))) {
            from.charSource().copyTo(into.charSink());
            return true;
        } else {
            return false;
        }
    }

    private boolean hasCharset(AbstractResource resource) {
        return resource.mediaType().charset().isPresent();
    }

    private Charset charset(AbstractResource resource) {
        return resource.mediaType().charset().get();
    }
}
