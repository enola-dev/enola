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

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Non-standard Enola specific "string:hello" Resource I/O implementation.
 *
 * <p>@deprecated Use {@link DataResource} (or {@link MultibaseResource}) instead of this!
 */
@Deprecated // TODO Replace all original StringResource usages with DataResource, and remove
// TODO Cannot be replaced, because data: cannot have #fragment - how about MultibaseResource?
public class StringResource extends BaseResource implements ReadableButNotWritableResource {

    public static class Provider implements ResourceProvider {
        @Override
        public Resource getResource(URI uri) {
            if (SCHEME.equals(uri.getScheme()))
                // NOT new StringResource(uriPath, mediaType),
                // because that is confusing, as it will chop off after # and interpret '?'
                // which is confusing for users, for this URI scheme. If "literal" resources
                // WITH MediaType are required, consider adding DataResource for data:
                return StringResource.of(uri.getSchemeSpecificPart());
            else return null;
        }
    }

    static final String SCHEME = "string";

    private final String string;

    /**
     * @deprecated Replace with {@link StringResource2#of(String, MediaType, URI)}
     */
    @Deprecated
    public static Resource of(@Nullable String text, MediaType mediaType, URI fragmentURI) {
        return StringResource2.of(text, mediaType, fragmentURI);
    }

    /**
     * @deprecated Replace with {@link DataResource#of(String, MediaType)}
     */
    @Deprecated
    public static Resource of(@Nullable String text, MediaType mediaType) {
        if (Strings.isNullOrEmpty(text)) {
            return new EmptyResource(mediaType);
        } else {
            return new StringResource(text, mediaType);
        }
    }

    /**
     * @deprecated Replace with {@link DataResource#of(String)}
     */
    @Deprecated
    public static Resource of(String text) {
        return of(text, MediaType.PLAIN_TEXT_UTF_8);
    }

    private StringResource(String text, MediaType mediaType) {
        this(text, mediaType, createURI(text));
    }

    private static URI createURI(String text) {
        try {
            return new URI(SCHEME, text, null);
        } catch (URISyntaxException e) {
            // This should never happen if the escaping done within URI is correct...
            throw new IllegalArgumentException("String is invalid in URI: " + text, e);
        }
    }

    protected StringResource(String text, MediaType mediaType, URI uri) {
        super(uri, mediaType);
        this.string = Objects.requireNonNull(text, "text");
        if ("".equals(text)) {
            throw new IllegalArgumentException(
                    "Empty string: not supported (because that's an invalid URI); please use #of()"
                            + " factory method instead");
        }
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
