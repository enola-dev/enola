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

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.OptionalConverter;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.yamljson.YamlJson;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.DynamicModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.util.Optional;

public class RdfReaderConverter implements OptionalConverter<ReadableResource, Model> {

    private final RdfReaderConverterInto converterInto = new RdfReaderConverterInto();

    @Override
    public Optional<Model> convert(ReadableResource input) throws ConversionException {
        // RDJ4j doesn't dig YAML, yet; so until it supports https://json-ld.github.io/yaml-ld/
        // we just Rosetta transform YAML to JSON and then pass that through to RDJ4j:
        if (MediaTypes.normalizedNoParamsEquals(
                input.mediaType(), RdfMediaTypeYamlLd.YAML_LD, YAML_UTF_8)) {
            var json = new MemoryResource(RdfMediaTypes.JSON_LD);
            YamlJson.YAML_TO_JSON.convertInto(input, json);
            input = json;
        }

        var model = new DynamicModel(new LinkedHashModelFactory());
        var handler = new StatementCollector(model);
        if (converterInto.convertInto(input, handler)) {
            return Optional.of(model);
        } else {
            return Optional.empty();
        }
    }
}
