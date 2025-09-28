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
package dev.enola.common.template.convert;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.template.TemplateProvider;

import java.util.Map;

public class Temply {

    private final ObjectReader objectReader;
    private final TemplateProvider templateProvider;

    public Temply(ObjectReader objectReader, TemplateProvider templateProvider) {
        this.objectReader = objectReader;
        this.templateProvider = templateProvider;
    }

    @SuppressWarnings("unchecked")
    public void convert(
            Iterable<ReadableResource> dataResources,
            ReadableResource templateResource,
            WritableResource into)
            throws Exception {

        var mapBuilder = ImmutableMap.builder();
        for (var dataResource : dataResources) {
            mapBuilder.putAll(objectReader.read(dataResource, Map.class));
        }
        mapBuilder.putAll(objectReader.read(templateResource, Map.class));

        var allDataMap = mapBuilder.build();
        var template = templateProvider.get(templateResource);
        try (var appendable = into.charSink().openBufferedStream()) {
            template.apply(allDataMap, appendable);
        }
    }
}
