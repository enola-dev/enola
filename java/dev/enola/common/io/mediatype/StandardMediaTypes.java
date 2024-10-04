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
package dev.enola.common.io.mediatype;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

public class StandardMediaTypes implements MediaTypeProvider {

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return ImmutableMultimap.of(
                ".html", MediaType.HTML_UTF_8,
                ".json", MediaType.JSON_UTF_8,
                ".css", MediaType.CSS_UTF_8,
                ".js", MediaType.JAVASCRIPT_UTF_8);
    }
}
