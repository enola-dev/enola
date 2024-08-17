/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.common.xml;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;

import java.util.Map;
import java.util.Set;

@AutoService(MediaTypeProvider.class)
public class XmlMediaType implements MediaTypeProvider {

    @Override
    public Map<String, MediaType> extensionsToTypes() {
        return ImmutableMap.of("xml", MediaType.XML_UTF_8);
    }

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return Map.of(MediaType.XML_UTF_8, Set.of(MediaType.parse("application/xml")));
    }
}
