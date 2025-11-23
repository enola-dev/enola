/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.yamljson.testlib;

import com.google.common.net.MediaType;
import com.google.common.truth.Truth;

import dev.enola.common.io.resource.ClasspathResource;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.IOException;

public final class TestYaml {
    private TestYaml() {}

    public static String write(Object object) {
        var options = new DumperOptions();
        // options.setVersion(DumperOptions.Version.V1_1);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setCanonical(false);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setIndentWithIndicator(true);
        options.setIndicatorIndent(2);
        options.setIndent(2);

        var beanYamler = new Yaml(new SnakeYamlRepresenter(options), options);
        beanYamler.setBeanAccess(BeanAccess.FIELD);
        return beanYamler.dump(object);
    }

    public static void assertEqualsTo(Object object, String expectedYAML) {
        Truth.assertThat(write(object)).isEqualTo(expectedYAML);
    }

    public static void assertEqualsToResource(Object object, String resourceName) {
        var resource = new ClasspathResource(resourceName, MediaType.PLAIN_TEXT_UTF_8);
        try {
            var actualYAML = write(object);
            var expectedYAML = resource.charSource().read();
            Truth.assertThat(actualYAML).isEqualTo(expectedYAML);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
