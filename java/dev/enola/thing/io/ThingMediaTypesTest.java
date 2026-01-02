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
package dev.enola.thing.io;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ResourceProviders;

import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class ThingMediaTypesTest {

    @Rule public SingletonRule r = $(MediaTypeProviders.set(new ThingMediaTypes()));

    @Rule
    public TestTLCRule rule =
            TestTLCRule.of(
                    MediaTypeProviders.class,
                    new MediaTypeProviders(new ThingMediaTypes(), new YamlMediaType()));

    @Test
    public void loaded() {
        assertThat(MediaTypeProviders.SINGLETON.get().extensionsToTypes())
                .containsKey(".thing.yaml");
    }

    @Test
    public void thingYAML() throws URISyntaxException {
        var rp = new ResourceProviders(new FileResource.Provider());
        var resource = rp.getResource(new URI("file:/picasso.thing.yaml"));
        assertThat(resource.mediaType()).isEqualTo(ThingMediaTypes.THING_YAML_UTF_8);
    }
}
