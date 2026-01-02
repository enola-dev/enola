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
package dev.enola.rdf.proto;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.common.convert.ConverterInto;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

public interface AbstractModelConverter<T>
        extends Converter<T, Model>, ConverterInto<T, RDFHandler> {
    // TODO Move AbstractModelConverter from rdf.proto to rdf, as it's not Proto specific

    @Override
    default Model convert(T input) throws ConversionException {
        var model = new ModelBuilder().build();
        var statementCollector = new StatementCollector(model);
        convertIntoOrThrow(input, statementCollector);
        return model;
    }
}
