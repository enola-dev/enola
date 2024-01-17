/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static org.junit.Assert.assertThat;

import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.YamlMediaType;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class URIsTest {

    @Test
    public void testGetQueryMap() throws URISyntaxException {
        assertThat(URIs.getQueryMap(null)).isEmpty();
        assertThat(URIs.getQueryMap(URI.create(""))).isEmpty();
        assertThat(URIs.getQueryMap(URI.create("http://www.vorburger.ch"))).isEmpty();
        assertThat(URIs.getQueryMap(URI.create("http://google.com?q=michi#fragment")))
                .containsExactly("q", "michi");
        assertThat(URIs.getQueryMap(URI.create("http://google.com?q=#fragment")))
                .containsExactly("q", "");
        assertThat(URIs.getQueryMap(URI.create("fd:1?charset=ASCII")))
                .containsExactly("charset", "ASCII");
        assertThat(URIs.getQueryMap(URI.create("fd:1?charset=ASCII#fragment")))
                .containsExactly("charset", "ASCII");
        assertThat(URIs.getQueryMap(URI.create("scheme:thing?ping=pong=pang#fragment")))
                .containsExactly("ping", "pong=pang");
    }

    @Test
    public void testHasNoMediaType() throws URISyntaxException {
        assertThat(URIs.getMediaType(URI.create("scheme:something")))
                .isEqualTo(MediaType.OCTET_STREAM.withCharset(Charset.defaultCharset()));
    }

    @Test
    public void testGetMediaType() throws URISyntaxException {
        var uri = URI.create("fd:1?something=else&mediaType=application/yaml;charset=utf-16be");
        assertThat(URIs.getQueryMap(uri))
                .containsExactly(
                        "something", "else", "mediatype", "application/yaml;charset=utf-16be");
        assertThat(URIs.getMediaType(uri))
                .isEqualTo(YamlMediaType.YAML_UTF_8.withCharset(StandardCharsets.UTF_16BE));
    }

    @Test
    public void testAddMediaType() throws URISyntaxException {
        var mt1 = MediaType.GIF;
        var uri1 = URIs.addMediaType(URI.create("scheme:something"), mt1);
        var uri1expected = URI.create("scheme:something?mediaType=image%2Fgif");
        assertThat(uri1).isEqualTo(uri1expected);
        assertThat(URIs.getMediaType(uri1expected))
                .isEqualTo(mt1.withCharset(Charset.defaultCharset()));

        var mt2 = MediaType.PLAIN_TEXT_UTF_8;
        var uri2 = URIs.addMediaType(URI.create("scheme:something"), mt2);
        var uri2expected = URI.create("scheme:something?mediaType=text%2Fplain%3Bcharset%3Dutf-8");
        assertThat(uri2).isEqualTo(uri2expected);
        assertThat(URIs.getMediaType(uri2expected)).isEqualTo(mt2);

        var uri3 = URIs.addMediaType(URI.create("scheme:?"), mt1);
        var uri3expected = URI.create("scheme:?mediaType=image%2Fgif");
        assertThat(uri3).isEqualTo(uri3expected);
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
        assertThat(URIs.getFilename(uri)).isEqualTo(expectedFilename);
    }

    /** See {@link URIs#getPath(String)} */
    @Test
    public void testGetPath() {
        var f = new File("/absolute/file?param=abc#anchor");
        // NOK! assertThat(f.toURI().getPath()).isEqualTo("/absolute/file");
        assertThat(URIs.getPath(f.toURI())).isEqualTo("/absolute/file");

        assertThat(URI.create("file:/absolute/file").getPath()).isEqualTo("/absolute/file");
        assertThat(URIs.getPath(URI.create("file:/absolute/file"))).isEqualTo("/absolute/file");

        // NOK! assertThat(URI.create("file:relative/file").getPath()).isEqualTo("relative/file");
        assertThat(URIs.getPath(URI.create("file:relative/file"))).isEqualTo("relative/file");
    }
}
