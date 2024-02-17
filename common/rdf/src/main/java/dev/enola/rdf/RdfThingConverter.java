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

import dev.enola.common.convert.BiConverter;
import dev.enola.common.convert.ConversionException;
import dev.enola.thing.Thing;

import org.eclipse.rdf4j.model.Model;

class RdfThingConverter implements BiConverter<Thing, Model> {

    @Override
    public Model convertTo(Thing input) throws ConversionException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertTo'");
    }

    @Override
    public Thing convertFrom(Model input) throws ConversionException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertFrom'");
    }
}
