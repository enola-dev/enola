/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.io.Resources;
import com.google.common.truth.Truth;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class URIsTest {

    @Test
    public void testGetQueryMap() throws URISyntaxException {
        assertThat(URIs.getQueryMap(null)).isEmpty();
        assertThat(URIs.getQueryMap(URI.create(""))).isEmpty();
        assertThat(URIs.getQueryMap(URI.create("http://www.vorburger.ch"))).isEmpty();
        assertThat(URIs.getQueryMap(URI.create("http://google.com?q=michi#fragment")))
                .containsExactly("q", "michi");
        assertThat(URIs.getQueryMap(URI.create("fd:1?charset=ASCII")))
                .containsExactly("charset", "ASCII");
        assertThat(URIs.getQueryMap(URI.create("fd:1?charset=ASCII#fragment")))
                .containsExactly("charset", "ASCII");
    }

    @Test
    public void testGetFilename() throws URISyntaxException {
        // Files
        assertName(URI.create(""), "");
        assertName(new File("test.txt").toURI(), "test.txt");
        assertName(new File("/test.txt").toURI(), "test.txt");
        assertName(new File("/home/test.txt").toURI(), "test.txt");
        assertName(new File("/").toURI(), "");
        assertName(new File("directory").toURI(), "directory");
        assertName(new File("/directory").toURI(), "directory");
        assertName(new File("/directory/").toURI(), "directory");
        assertName(new File(".").toURI(), "");
        assertName(new File(".").getAbsoluteFile().toURI(), "");
        assertName(new File("./").toURI(), "");
        assertName(new File("./").getAbsoluteFile().toURI(), "");

        // Windows Files
        // TODO assertThat(new File("C:\\WINDOWS\\logo.bmp").toURI(), "logo.bmp");

        // HTTP
        assertName(URI.create("http://www.vorburger.ch"), "");
        assertName(URI.create("https://www.vorburger.ch"), "");
        assertName(URI.create("https://www.vorburger.ch/"), "");
        assertName(URI.create("https://www.vorburger.ch/index.html"), "index.html");
        assertName(URI.create("https://www.vorburger.ch/index.html#toot"), "index.html");
        assertName(URI.create("https://www.vorburger.ch/index.html?search=1998"), "index.html");
        assertName(URI.create("https://www.vorburger.ch/projects/"), "");
        assertName(
                URI.create("https://www.vorburger.ch/projects/coc/coc_src.html"), "coc_src.html");
        assertName(URI.create("https://www.vorburger.ch/space%20file"), "space file");

        // Classpath Resources
        assertName(Resources.getResource("empty").toURI(), "empty");
        assertName(
                Resources.getResource(
                                "META-INF/services/dev.enola.common.io.mediatype.MediaTypeProvider")
                        .toURI(),
                "dev.enola.common.io.mediatype.MediaTypeProvider");

        // No schema - this is correct!
        assertName(URI.create("test.txt"), "");
        assertName(URI.create(""), "");

        Assert.assertThrows(NullPointerException.class, () -> URI.create(null));
    }

    private void assertName(URI uri, String expectedFilename) {
        Truth.assertThat(URIs.getFilename(uri)).isEqualTo(expectedFilename);
    }
}
