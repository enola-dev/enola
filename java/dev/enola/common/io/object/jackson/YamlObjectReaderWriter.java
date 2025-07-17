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
package dev.enola.common.io.object.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;

public class YamlObjectReaderWriter extends JacksonObjectReaderWriter {

    private static ObjectMapper newObjectMapper() {
        // NB: Keep in-sync with the similar (but not the same, different API!) in
        //       the dev.enola.common.yamljson.YAML class.
        var loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        loaderOptions.setAllowRecursiveKeys(false);
        loaderOptions.setCodePointLimit(10 * 1024 * 1024); // 10 MB

        var dumperOptions = new DumperOptions();
        dumperOptions.setExplicitStart(false);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);

        var yamlFactory =
                YAMLFactory.builder()
                        .loaderOptions(loaderOptions)
                        .dumperOptions(dumperOptions)
                        .build();

        var yamlMapper = new YAMLMapper(yamlFactory);
        yamlMapper.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlMapper.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        return yamlMapper;
    }

    public YamlObjectReaderWriter() {
        super(newObjectMapper());
    }

    @Override
    boolean canHandle(MediaType mediaType) {
        return MediaTypes.normalizedNoParamsEquals(mediaType, YamlMediaType.YAML_UTF_8);
    }

    @Override
    String empty() {
        return "{}";
    }
}
