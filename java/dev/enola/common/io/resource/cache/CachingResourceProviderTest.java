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
package dev.enola.common.io.resource.cache;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.Resource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.StringResource;
import dev.enola.common.io.resource.cache.ClasspathCacheResourceProvider.ClasspathLocationWithMediaType;

import org.jspecify.annotations.Nullable;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CachingResourceProviderTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set());

    private final URI TEST_URI = URI.create("test:it");

    private class TestResourceProvider implements ResourceProvider {

        boolean wasCalled = false;

        @Override
        public @Nullable Resource getResource(URI uri) {
            wasCalled = true;
            if (uri.equals(TEST_URI)) return StringResource.of("hello", MediaType.PLAIN_TEXT_UTF_8);
            else return null;
        }
    }

    private final TestResourceProvider trp = new TestResourceProvider();

    @Test
    public void cache() throws IOException {
        var rp = new AlwaysCachingResourceProvider(trp);
        assertThat(trp.wasCalled).isFalse();

        assertThat(rp.getResource(TEST_URI).charSource().read()).isEqualTo("hello");
        assertThat(trp.wasCalled).isTrue();

        trp.wasCalled = false;
        assertThat(rp.getResource(TEST_URI).charSource().read()).isEqualTo("hello");
        assertThat(trp.wasCalled).isFalse();

        assertThat(rp.get("test:nada")).isNull();
    }

    @Test
    public void classpath() throws IOException {
        var uri = "http://some.where/testX"; // /test uses MediaTypesTest's application/test!
        var mt = MediaType.PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.US_ASCII);
        var rp =
                new ClasspathCacheResourceProvider(
                        ImmutableMap.of(
                                URI.create(uri),
                                new ClasspathLocationWithMediaType("test-hello-ascii.txt", mt)));

        var resource = rp.get(uri);
        assertThat(resource.charSource().read()).isEqualTo("hello, world\n");
        assertThat(resource.mediaType()).isEqualTo(mt);
        assertThat(resource.uri()).isEqualTo(URI.create(uri));
    }
}
