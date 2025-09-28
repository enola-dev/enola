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

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.common.template.TemplateProvider;

import java.util.HashMap;
import java.util.Map;

// @NotThreadSafe
public class TemplateResourceConverter implements CatchingResourceConverter {

    // TODO Expose this as an ./enola template CLI sub-command

    private final TemplateProvider templateProvider;
    private final Map<String, Object> data = new HashMap<>();

    public TemplateResourceConverter(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    public void putAll(Map<String, Object> moreData) {
        this.data.putAll(moreData);
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {
        var template = templateProvider.get(from);
        try (var appendable = into.charSink().openBufferedStream()) {
            template.apply(data, appendable);
        }
        return true;
    }
}
