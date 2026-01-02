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
package dev.enola.rdf.io;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.OptionalConverter;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.DynamicModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.IOException;
import java.util.Optional;

/** Reads a {@link ReadableResource} into an RDFJ4j {@link org.eclipse.rdf4j.model.Model}. */
public class RdfReaderConverter implements OptionalConverter<ReadableResource, Model> {

    private final RdfReaderConverterInto converterInto;

    public RdfReaderConverter(ResourceProvider rp) {
        this.converterInto = new RdfReaderConverterInto(rp);
    }

    @Override
    public Optional<Model> convert(ReadableResource from) throws ConversionException {
        try {
            if (from.byteSource().isEmpty()) return Optional.empty();
        } catch (IOException e) {
            throw new ConversionException("isEmpty failed: " + from.uri(), e);
        }

        var model = new DynamicModel(new LinkedHashModelFactory());
        var handler = new StatementCollector(model);
        if (converterInto.convertInto(from, handler)) {
            return Optional.of(model);
        } else {
            return Optional.empty();
        }
    }
}
