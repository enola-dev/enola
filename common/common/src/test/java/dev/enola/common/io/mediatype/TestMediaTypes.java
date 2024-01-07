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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

import java.util.Map;
import java.util.Set;

// TODO @AutoService(MediaTypeProvider.class)
public class TestMediaTypes implements MediaTypeProvider {

    public static final MediaType TEST = MediaType.create("application", "test");

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return ImmutableMap.of(
                TEST, Sets.newHashSet(MediaType.create("application", "test-alternative")));
    }

    @Override
    public Map<String, MediaType> extensionsToTypes() {
        return null;
    }
}
