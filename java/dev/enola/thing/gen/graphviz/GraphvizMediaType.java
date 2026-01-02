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
package dev.enola.thing.gen.graphviz;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;

import java.nio.charset.StandardCharsets;

/**
 * MediaType of the <code>text/vnd.graphviz</code> <a
 * href="https://en.wikipedia.org/wiki/DOT_(graph_description_language)">Graph Description
 * Language</a> DOT (<code>*.gv</code>) for <a
 * href="https://en.wikipedia.org/wiki/Graphviz">Graphviz</a>, in <a
 * href="https://graphviz.org/doc/info/lang.html#character-encodings">UTF-8 Character Encoding</a>.
 */
public class GraphvizMediaType implements MediaTypeProvider {

    public static final MediaType GV =
            MediaType.create("text", "vnd.graphviz").withCharset(StandardCharsets.UTF_8);

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return ImmutableMultimap.of(".gv", GV);
    }

    // TODO Implement detect() to support alternative https://graphviz.org/docs/attrs/charset/ ...
}
