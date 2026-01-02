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
import com.google.common.net.MediaType;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Resources which "splits" a "base" resource into "parts", based on Regular Expression named
 * capturing groups.
 */
public class RegexMultipartResource extends DelegatingMultipartResource {

    public RegexMultipartResource(ReadableResource baseResource, PartsDef defs) throws IOException {
        super(baseResource, split(baseResource, defs));
    }

    protected record PartsDef(String regex, Map<String, MediaType> mediaTypes) {}

    private static ImmutableMap<String, Resource> split(
            ReadableResource baseResource, PartsDef defs) throws IOException {
        // TODO #performance Refactor this, so that e.g. MarkdownResource can compile the Pattern
        // once only
        var pattern = Pattern.compile(defs.regex, Pattern.DOTALL | Pattern.MULTILINE);
        var groups = pattern.namedGroups();
        var keys = groups.keySet();

        var text = baseResource.charSource().read();
        var matcher = pattern.matcher(text);

        var n = groups.size();
        var usedPartNames = new HashSet<String>(n);
        var parts = ImmutableMap.<String, Resource>builderWithExpectedSize(n);
        if (matcher.find()) {
            for (var key : keys) {
                String part = matcher.group(key);
                var fragmentURI = partFragmentURI(baseResource, key);
                var mediaType = mediaType(baseResource, defs.mediaTypes.get(key), key);
                var resource = StringResource.of(part, mediaType, fragmentURI);
                parts.put(key, resource);
                usedPartNames.add(key);
            }
        }

        for (var key : keys) {
            if (!usedPartNames.contains(key)) {
                var fragmentURI = partFragmentURI(baseResource, key);
                var mediaType = mediaType(baseResource, defs.mediaTypes.get(key), key);
                var empty = new EmptyResource(fragmentURI, mediaType);
                parts.put(key, empty);
            }
        }

        return parts.build();
    }

    private static URI partFragmentURI(ReadableResource baseResource, String key) {
        return baseResource.uri().resolve("#" + key);
    }

    private static MediaType mediaType(
            ReadableResource baseResource, MediaType partMediaType, String key) {
        var baseMediaType = baseResource.mediaType();
        if (partMediaType == null) partMediaType = baseMediaType;
        if (!partMediaType.charset().isPresent()) {
            var baseCharset = baseMediaType.charset();
            if (baseCharset.isPresent())
                partMediaType = partMediaType.withCharset(baseCharset.get());
            else {
                var uri = baseResource.uri() + "#" + key;
                throw new IllegalArgumentException(
                        "Missing Charset on both base and part resources for: " + uri);
            }
        }
        return partMediaType;
    }
}
