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
package dev.enola.rdf;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.common.io.resource.ReadableResource;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.DynamicModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

public class RdfReaderConverter implements Converter<ReadableResource, Model> {

    private final RdfReaderConverterInto converterInto = new RdfReaderConverterInto();

    @Override
    public Model convert(ReadableResource input) throws ConversionException {
        var model = new DynamicModel(new LinkedHashModelFactory());
        var handler = new StatementCollector(model);
        if (!converterInto.convertInto(input, handler)) {
            throw new ConversionException("No RDFFormat for: " + input);
        }
        return model;
    }
}
