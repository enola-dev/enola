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
package dev.enola.format.tika;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;

public class TikaMediaTypeProviderTest {

    @Test
    public void detectHtml() {
        var mtp = new TikaMediaTypeProvider();
        var mt = mtp.detect(new ClasspathResource("test.html"));
        assertThat(mt).hasValue(MediaType.HTML_UTF_8);
    }

    @Test
    @Ignore // TODO FIXME
    public void detectCBL() {
        var mtp = new TikaMediaTypeProvider();
        var mt = mtp.detect(new FileResource(URI.create("file:///test.CBL")));
        assertThat(mt).hasValue(MediaType.parse("text/x-cobol"));
    }

    @Test
    @Ignore // TODO FIXME
    public void detectWarcGz() {
        var mtp = new TikaMediaTypeProvider();
        var mt = mtp.detect(new FileResource(URI.create("file:///test.warc.gz")));
        assertThat(mt).hasValue(MediaType.parse("application/warc+gz"));
    }

    @Test
    public void knownTypesWithAlternatives() {
        var mtp = new TikaMediaTypeProvider();
        assertThat(mtp.knownTypesWithAlternatives().keySet()).isNotEmpty();
    }

    @Test
    public void extensionsToTypes() {
        var mtp = new TikaMediaTypeProvider();
        assertThat(mtp.extensionsToTypes()).isNotEmpty();
        assertThat(mtp.extensionsToTypes().keySet().iterator().next().startsWith(".")).isTrue();
    }
}
