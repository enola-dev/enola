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

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.MediaTypes;

import io.ipfs.multibase.Multibase;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Resource I/O implementation based on a <a
 * href="https://github.com/multiformats/multibase">multibase: URLs</a> (invented by Enola.dev; not
 * standardized anywhere, yet).
 *
 * <p>@see {@link DataResource} for another similar URL scheme.
 */
public class MultibaseResource extends BaseResource implements ReadableButNotWritableResource {

    // Intentionally different from MultibaseIRI's mb: scheme.
    // multibase: is for fetching resources, mb: is for Thing IRIs.
    private static final String SCHEME = "multibase";

    public static class Provider implements ResourceProvider {
        @Override
        public Resource getResource(URI uri) {
            if (SCHEME.equals(uri.getScheme())) return new MultibaseResource(uri);
            else return null;
        }
    }

    private final ByteSource byteSource;

    public static Resource of(@Nullable String text, @Nullable MediaType mediaType) {
        var data = MediaTypes.toStringWithoutSpaces(mediaType) + "," + Strings.nullToEmpty(text);
        try {
            return new MultibaseResource(new URI(SCHEME, data, null));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(data, e);
        }
    }

    public static Resource of(@Nullable String text) {
        return of(text, null);
    }

    // TODO Rewrite using https://blog.jetbrains.com/idea/2024/02/constructor-makeover-in-java-22/
    // once we're on Java >21 (and avoid having to match the RegExp twice, which is inefficient)
    private static URI checkSchema(URI uri) {
        if (!SCHEME.equals(uri.getScheme())) throw new IllegalArgumentException(uri.toString());
        return uri;
    }

    public MultibaseResource(URI uri) {
        super(checkSchema(uri));
        String data = URIs.dropQueryAndFragment(uri).getSchemeSpecificPart();
        byteSource = ByteSource.wrap(Multibase.decode(data));
    }

    @Override
    public ByteSource byteSource() {
        return byteSource;
    }
}
