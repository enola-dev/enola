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
package dev.enola.common.io.mediatype;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.resource.BaseResource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;

class MediaTypeProviderTest {

    @RegisterExtension SingletonRule r = $(MediaTypeProviders.set(new TestMediaType()));

    @Test
    void match() {
        var uri = URI.create("test:MediaTypeProviderTest");
        var resource = new TestAbstractResource(uri, TestMediaType.TEST);
        assertThat(resource.mediaType()).isEqualTo(TestMediaType.TEST);
    }

    @Test
    void alternative() {
        var uri = URI.create("test:MediaTypeProviderTest");
        var resource = new TestAbstractResource(uri, TestMediaType.TEST_ALTERNATIVE);
        var tmt = MediaTypeProviders.SINGLETON.get();
        assertThat(tmt.detect(resource)).hasValue(TestMediaType.TEST);
        // TODO assertThat(resource.mediaType()).isEqualTo(TestMediaType.TEST);
    }

    @Test
    void extension() {
        var uri = URI.create("test:MediaTypeProviderTest.test");
        var resource = new TestAbstractResource(uri, MediaType.ANY_TYPE);
        var tmt = MediaTypeProviders.SINGLETON.get();
        assertThat(tmt.detect(resource)).hasValue(TestMediaType.TEST);
        // TODO assertThat(resource.mediaType()).isEqualTo(TestMediaType.TEST);
    }

    private static class TestAbstractResource extends BaseResource {
        protected TestAbstractResource(URI uri, MediaType mediaType) {
            super(uri, mediaType);
        }
    }
}
