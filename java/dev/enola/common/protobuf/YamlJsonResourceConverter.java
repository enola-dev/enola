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
package dev.enola.common.protobuf;

import static com.google.common.net.MediaType.JSON_UTF_8;

import static dev.enola.common.io.mediatype.MediaTypes.normalizedNoParamsEquals;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_JSON_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_YAML_UTF_8;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.Resource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.common.yamljson.YamlJson;

import java.io.IOException;

/**
 * Utility to convert YAML ⇔ JSON {@link Resource}s.
 *
 * @see MessageResourceConverter
 * @see YamlJson
 */
public class YamlJsonResourceConverter implements CatchingResourceConverter {

    // TODO This class ideally should be in package dev.enola.common.yamljson,
    // but as-is it cannot, because it references ProtobufMediaTypes directly...
    // this could be avoided with some sort of smarter MediaType handling for +json/+yaml MIME
    // sub-types.

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws IOException, ConversionException {
        var fromMT = from.mediaType().withoutParameters();
        var intoMT = into.mediaType().withoutParameters();

        if (normalizedNoParamsEquals(fromMT, PROTOBUF_JSON_UTF_8, JSON_UTF_8)
                && normalizedNoParamsEquals(intoMT, PROTOBUF_YAML_UTF_8, YAML_UTF_8)) {

            return YamlJson.JSON_TO_YAML.convertInto(from, into);

        } else if (normalizedNoParamsEquals(fromMT, PROTOBUF_YAML_UTF_8, YAML_UTF_8)
                && normalizedNoParamsEquals(intoMT, PROTOBUF_JSON_UTF_8, JSON_UTF_8)) {

            return YamlJson.YAML_TO_JSON.convertInto(from, into);

        } else {
            return false;
        }
    }
}
