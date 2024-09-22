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
package dev.enola.rdf.io;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;

public class RdfResourceConverter implements CatchingResourceConverter {

    // TODO Add missing tests for this this class (it may well not work as-is yet)

    // TODO Add conversion to/from Thing (incl. "chaining" to Thing YAML/JSON/BinPB)

    private final RdfReaderConverterInto reader;

    public RdfResourceConverter(ResourceProvider resourceProvider) {
        this.reader = new RdfReaderConverterInto(resourceProvider);
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {

        var opt = WritableResourceRDFHandler.create(into);
        if (opt.isEmpty()) return false;

        try (var handler = opt.get()) {
            return reader.convertInto(from, handler);
        }
    }
}
