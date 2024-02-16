/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;

import java.util.Map;
import java.util.Set;

public class YamlMediaType implements MediaTypeProvider {

    // https://www.ietf.org/archive/id/draft-ietf-httpapi-yaml-mediatypes-00.html
    // https://github.com/ietf-wg-httpapi/mediatypes/blob/main/draft-ietf-httpapi-yaml-mediatypes.md

    // https://stackoverflow.com/questions/488694/how-to-set-the-character-encoding-in-a-yaml-file

    public static final MediaType YAML_UTF_8 =
            create("application", "yaml").withCharset(Charsets.UTF_8);

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
    public Map<String, MediaType> extensionsToTypes() {
        return ImmutableMap.of("yaml", YAML_UTF_8, "yml", YAML_UTF_8);
    }
}
