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
package dev.enola.format.tika;

import static com.google.common.net.MediaType.OCTET_STREAM;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.FileResource;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;

public class TikaMediaTypeProviderTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new TikaMediaTypeProvider()));

    @Test
    public void detectHtml() {
        var r = new ClasspathResource("test.html");
        assertThat(r.mediaType()).isEqualTo(MediaType.HTML_UTF_8);
    }

    @Test
    public void detectCBL() {
        var r = new FileResource(URI.create("file:///test.CBL"));
        assertThat(r.mediaType()).isEqualTo(MediaType.parse("text/x-cobol").withCharset(UTF_8));
    }

    @Test
    @Ignore // TODO FIXME Debug and fix why this still doesn't work
    public void detectWarcGz() {
        var r = new FileResource(URI.create("file:///test.warc.gz"));
        assertThat(r.mediaType()).isEqualTo(MediaType.parse("application/warc+gz"));
    }

    @Test
    public void knownTypesWithAlternatives() {
        assertThat(MediaTypeProviders.SINGLETON.get().knownTypesWithAlternatives().keySet())
                .isNotEmpty();
    }

    @Test
    public void extensionsToTypes() {
        var mediaTypeProviders = MediaTypeProviders.SINGLETON.get();
        assertThat(mediaTypeProviders.extensionsToTypes()).isNotEmpty();
        assertThat(
                        mediaTypeProviders
                                .extensionsToTypes()
                                .keySet()
                                .iterator()
                                .next()
                                .startsWith("."))
                .isTrue();
    }

    @Test
    public void exclusions() {
        isExcluded(".gv");
    }

    private void isExcluded(String extension) {
        var mediaTypeProviders = MediaTypeProviders.SINGLETON.get();
        assertThat(mediaTypeProviders.extensionsToTypes().keySet()).doesNotContain(extension);
        assertThat(mediaTypeProviders.detect(new EmptyResource(URI.create("test" + extension))))
                .hasValue(OCTET_STREAM);
    }
}
