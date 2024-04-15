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
package dev.enola.common.io.mediatype;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.AbstractResource;

import org.junit.Test;

import java.net.URI;

public class MediaTypeProviderTest {

    MediaTypesTest tmt = new MediaTypesTest();

    @Test
    public void nomatch() {
        var uri = URI.create("test:MediaTypeProviderTest");
        var resource = new TestAbstractResource(uri, MediaType.OGG_VIDEO);
        assertThat(tmt.detect(resource)).isEmpty();
    }

    @Test
    public void match() {
        var uri = URI.create("test:MediaTypeProviderTest");
        var resource = new TestAbstractResource(uri, MediaTypesTest.TEST);
        assertThat(tmt.detect(resource)).hasValue(MediaTypesTest.TEST);
    }

    @Test
    public void alternative() {
        var uri = URI.create("test:MediaTypeProviderTest");
        var resource = new TestAbstractResource(uri, MediaTypesTest.TEST_ALTERNATIVE);
        assertThat(tmt.detect(resource)).hasValue(MediaTypesTest.TEST);
    }

    @Test
    public void extension() {
        var uri = URI.create("test:MediaTypeProviderTest.test");
        var resource = new TestAbstractResource(uri, MediaType.ANY_TYPE);
        assertThat(tmt.detect(resource)).hasValue(MediaTypesTest.TEST);
    }

    private static record TestAbstractResource(URI uri, MediaType mediaType)
            implements AbstractResource {}
}
