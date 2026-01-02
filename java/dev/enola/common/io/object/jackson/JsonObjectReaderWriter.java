/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypes;

public class JsonObjectReaderWriter extends JacksonObjectReaderWriter {

    public JsonObjectReaderWriter(ObjectMapper om) {
        super(om);
    }

    public JsonObjectReaderWriter() {
        this(new ObjectMapper());
    }

    @Override
    boolean canHandle(MediaType mediaType) {
        return MediaTypes.normalizedNoParamsEquals(mediaType, MediaType.JSON_UTF_8);
    }

    @Override
    String empty() {
        return "{}";
    }
}
