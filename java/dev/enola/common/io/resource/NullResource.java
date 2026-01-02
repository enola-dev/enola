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

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Resource which ignores writes, and returns an infinite amount of bytes of value 0 on read. This
 * is a bit like /dev/null on *NIX OS for writing, but not for reading (because /dev/null returns
 * EOF on read, but this does not).
 *
 * @see EmptyResource for an (non-writable) EOF ReadableResource
 */
public class NullResource extends BaseResource implements Resource {

    public static class Provider implements ResourceProvider {

        @Override
        public Resource getResource(URI uri) {
            if (SCHEME.equals(uri.getScheme())) return NullResource.INSTANCE;
            else return null;
        }
    }

    public static final NullResource INSTANCE =
            new NullResource(MediaType.OCTET_STREAM.withCharset(StandardCharsets.UTF_8));

    static final String SCHEME = "null";

    public NullResource(MediaType mediaType) {
        super(uri(mediaType), mediaType);
    }

    private static URI uri(MediaType mediaType) {
        // TODO Why URI of MediaType, instead of a single hard-coded fixed one?
        // TODO Why withoutParameters()? To remove spaces? Just encode!
        return URI.create(SCHEME + ":" + mediaType.withoutParameters());
    }

    @Override
    public ByteSink byteSink() {
        return NullByteSink.INSTANCE;
    }

    @Override
    public ByteSource byteSource() {
        return NullByteSource.INSTANCE;
    }
}
