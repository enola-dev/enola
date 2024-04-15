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
package dev.enola.thing;

import static java.util.Collections.emptySet;

import com.google.auto.service.AutoService;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;
import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.thing.proto.Thing;

import java.util.Map;
import java.util.Set;

@AutoService(MediaTypeProvider.class)
public class ThingMediaTypes implements MediaTypeProvider {

    public static final MediaType THING_TEXTPROTO_UTF_8 =
            ProtobufMediaTypes.setProtoMessageFQN(
                    ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8,
                    Thing.getDescriptor().getFullName());

    public static final MediaType THING_BINARYPROTO_UTF_8 =
            ProtobufMediaTypes.setProtoMessageFQN(
                    ProtobufMediaTypes.PROTOBUF_BINARY, Thing.getDescriptor().getFullName());

    private static final String THING_SUBTYPE = "enola.dev#thing";

    public static final MediaType THING_JSON_UTF_8 =
            MediaType.create("text", THING_SUBTYPE + "+json").withCharset(Charsets.UTF_8);

    public static final MediaType THING_YAML_UTF_8 =
            MediaType.create("text", THING_SUBTYPE + "+yaml").withCharset(Charsets.UTF_8);

    public static final MediaType THING_HTML_UTF_8 =
            MediaType.create("text", THING_SUBTYPE + "+html").withCharset(Charsets.UTF_8);

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return ImmutableMap.of(
                THING_TEXTPROTO_UTF_8,
                emptySet(),
                THING_BINARYPROTO_UTF_8,
                emptySet(),
                THING_JSON_UTF_8,
                emptySet(),
                THING_YAML_UTF_8,
                emptySet(),
                THING_HTML_UTF_8,
                emptySet());
    }

    @Override
    public Map<String, MediaType> extensionsToTypes() {
        return ImmutableMap.of(
                "thing.txtpb",
                THING_TEXTPROTO_UTF_8,
                "thing.binpb",
                THING_BINARYPROTO_UTF_8,
                "thing.json",
                THING_JSON_UTF_8,
                "thing.yaml",
                THING_YAML_UTF_8,
                "thing.html",
                THING_HTML_UTF_8);
    }
}
