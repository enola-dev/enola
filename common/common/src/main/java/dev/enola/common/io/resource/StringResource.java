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

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class StringResource implements ReadableResource {

    public static final String SCHEME = "string";

    private static final MediaType MEDIA_TYPE = MediaType.PLAIN_TEXT_UTF_8;

    private final String string;
    private final URI uri;

    public StringResource(String s) {
        this.string = Objects.requireNonNull(s);
        try {
            if (!s.isEmpty()) {
                this.uri = new URI(SCHEME, string, null);
            } else {
                this.uri = EmptyResource.INSTANCE.uri();
            }

        } catch (URISyntaxException e) {
            // This should never happen, if the escaping above is correct...
            throw new IllegalArgumentException("String is invalid in URI: " + s, e);
        }
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public MediaType mediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public ByteSource byteSource() {
        return charSource().asByteSource(MEDIA_TYPE.charset().get());
    }

    @Override
    public CharSource charSource() {
        return CharSource.wrap(string);
    }
}
