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
import dev.enola.common.convert.ConverterInto;
import dev.enola.thing.Thing;

import org.eclipse.rdf4j.model.Model;

import java.io.IOException;

/** Converts an Enola {@link Thing} API into an RDF4j {@link Model}. */
public class ThingsRdfConverter implements ConverterInto<Thing, Model> {

    // TODO Implement ThingsRdfConverter (directly from [new] Java Thing API, no longer Proto Thing)

    @Override
    public boolean convertInto(Thing from, Model into) throws ConversionException, IOException {
        throw new IllegalStateException("TODO Implement me! ;-)");
    }
}
