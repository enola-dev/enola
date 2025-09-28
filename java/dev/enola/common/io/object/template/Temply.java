/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object.template;

import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.ObjectReaderWriter;
import dev.enola.common.io.object.ObjectWriter;
import dev.enola.common.io.object.jackson.JacksonObjectReaderWriterChain;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;

import java.util.Map;

public class Temply implements CatchingResourceConverter {

    // TODO Rename, to avoid name conflict with dev.enola.common.template.convert.Temply

    // TODO Remove; as replaced by TemplateResourceConverter?

    private final ObjectReader templatedObjectReader;
    private final ObjectWriter outputObjectWriter;

    public Temply(ObjectReaderWriter delegate) {
        this.templatedObjectReader = new TemplatedObjectReader(delegate);
        this.outputObjectWriter = delegate;
    }

    public Temply() {
        this(new JacksonObjectReaderWriterChain());
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {
        var map = templatedObjectReader.read(from, Map.class);
        return outputObjectWriter.write(map, into);
    }
}
