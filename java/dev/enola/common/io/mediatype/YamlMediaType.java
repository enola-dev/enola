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
package dev.enola.common.io.mediatype;

import static com.google.common.net.MediaType.create;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class YamlMediaType extends ResourceCharsetDetectorSPI implements MediaTypeProvider {
    // TODO Rename YamlMediaType to YamlMediaTypeProvider
    // TODO Move this into e.g. dev.enola.format.yaml ?

    // TODO How to allow this to express mediaType.subtype().endsWith("+yaml") ?

    // https://www.ietf.org/archive/id/draft-ietf-httpapi-yaml-mediatypes-00.html
    // https://github.com/ietf-wg-httpapi/mediatypes/blob/main/draft-ietf-httpapi-yaml-mediatypes.md

    // https://stackoverflow.com/questions/488694/how-to-set-the-character-encoding-in-a-yaml-file

    public static final MediaType YAML_UTF_8 =
            create("application", "yaml").withCharset(StandardCharsets.UTF_8);

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return ImmutableMap.of(
                YAML_UTF_8,
                ImmutableSet.of(
                        create("text", "yaml"),
                        create("text", "x-yaml"),
                        create("application", "x-yaml")));
    }

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return ImmutableMultimap.of(".yaml", YAML_UTF_8, ".yml", YAML_UTF_8);
    }

    @Override
    @SuppressWarnings("ComparisonOutOfRange") // TX ErrorProne, but it's OK here!
    public Optional<Charset> detectCharset(URI uri, ByteSource source) {
        byte[] header = peek(4, uri, source);
        if (header.length != 4) return Optional.of(StandardCharsets.UTF_8);

        // TODO Add test coverage for this to MediaTypeDetectorTest. Do BOMs have to be skipped?!
        // See https://yaml.org/spec/1.2.2/#52-character-encodings
        if (header[0] == 0 && header[1] == 0 && header[2] == 0xFE && header[3] == 0xFF)
            return Optional.of(MoreCharsets.UTF_32BE);

        if (header[0] == 0 && header[1] == 0 && header[2] == 0)
            return Optional.of(MoreCharsets.UTF_32BE);

        if (header[0] == 0xFF && header[1] == 0xFE && header[2] == 0 && header[3] == 0)
            return Optional.of(MoreCharsets.UTF_32LE);

        if (header[1] == 0 && header[2] == 0 && header[3] == 0)
            return Optional.of(MoreCharsets.UTF_32LE);

        if (header[0] == 0xFE && header[1] == 0xFF) return Optional.of(StandardCharsets.UTF_16BE);

        if (header[0] == 0 && header[1] == 0xFF) return Optional.of(StandardCharsets.UTF_16BE);

        if (header[0] == 0xFF && header[1] == 0xFE) return Optional.of(StandardCharsets.UTF_16LE);

        if (header[1] == 0) return Optional.of(StandardCharsets.UTF_16LE);

        if (header[0] == 0xEF && header[1] == 0xBB && header[3] == 0xBF)
            return Optional.of(StandardCharsets.UTF_8);

        return Optional.of(StandardCharsets.UTF_8);
    }
}
