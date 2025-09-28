/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

/** Resource based on String with a given URI and MediaType. */
public class StringResource2 extends BaseResource implements ReadableButNotWritableResource {

    private final String string;

    public static Resource of(@Nullable String text, MediaType mediaType, URI uri) {
        return new StringResource2(Strings.nullToEmpty(text), mediaType, uri);
    }

    private StringResource2(String text, MediaType mediaType, URI uri) {
        super(uri, mediaType);
        this.string = Objects.requireNonNull(text, "text");
        if (!mediaType.charset().isPresent()) {
            throw new IllegalArgumentException(
                    "MediaType is missing required charset: " + mediaType);
        }
    }

    @Override
    public ByteSource byteSource() {
        return charSource().asByteSource(mediaType().charset().get());
    }

    @Override
    public CharSource charSource() {
        return CharSource.wrap(string);
    }
}
