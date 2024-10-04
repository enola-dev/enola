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
package dev.enola.thing.io;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.mediatype.MediaTypeProvider;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ResourceProviders;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class ThingMediaTypesTest {

    @Test
    public void loaded() {
        assertThat(MediaTypeProviders.SINGLETON.extensionsToTypes()).containsKey(".thing.yaml");
    }

    @Test
    public void viaThingMediaTypes() throws URISyntaxException {
        check(new ThingMediaTypes());
    }

    @Test
    @Ignore // TODO This requires supporting an "order" (precedence) in MediaTypeProvider
    public void viaMediaTypeProviders() throws URISyntaxException {
        check(MediaTypeProviders.SINGLETON);
    }

    private void check(MediaTypeProvider mediaTypeProvider) throws URISyntaxException {
        var rp = new ResourceProviders(new FileResource.Provider());
        var resource = rp.getResource(new URI("file:/picasso.thing.yaml"));
        var mediaType = mediaTypeProvider.detect(resource).get();
        assertThat(mediaType).isEqualTo(ThingMediaTypes.THING_YAML_UTF_8);
    }
}
