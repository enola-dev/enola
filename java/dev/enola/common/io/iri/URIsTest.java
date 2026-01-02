/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.iri;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import static java.net.URI.create;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.net.MediaType;

import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs.MediaTypeAndOrCharset;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class URIsTest {

    @Test
    public void testGetQueryMap() throws URISyntaxException {
        assertThat(URIs.getQueryMap((URI) null)).isEmpty();
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

        // Ensure that query parameters are not artificially lower-cased, but kept as-is:
        assertThat(URIs.getQueryMap(URI.create("claude://?model=haiku&maxOutputTokens=512")))
                .containsExactly("model", "haiku", "maxOutputTokens", "512");

        // Glob URIs
        assertThat(URIs.getQueryMap("file:/tmp//?.txt")).isEmpty();
        assertThat(URIs.getQueryMap("file:/tmp//?q=.")).containsExactly("q", ".");
        // TODO assertThat(URIs.getQueryMap(URI.create("file:/tmp//?.txt"))).isEmpty();
    }

    @Test
    public void testHasNoMediaType() throws URISyntaxException {
        assertThat(URIs.getMediaTypeAndCharset(URI.create("scheme:something")))
                .isEqualTo(new MediaTypeAndOrCharset(null, null));
    }

    @Test
    public void testGetMediaType() throws URISyntaxException {
        var uri = URI.create("fd:1?something=else&mediaType=application/yaml;charset=utf-16be");
        assertThat(URIs.getQueryMap(uri))
                .containsExactly(
                        "something", "else", "mediaType", "application/yaml;charset=utf-16be");
        assertThat(URIs.getMediaTypeAndCharset(uri))
                .isEqualTo(new MediaTypeAndOrCharset("application/yaml;charset=utf-16be", null));
    }

    @Test
    public void testAddMediaType() throws URISyntaxException {
        var mt1 = MediaType.GIF;
        var uri1 = URIs.addMediaType(URI.create("scheme:something"), mt1);
        var uri1expected = URI.create("scheme:something?mediaType=image%2Fgif");
        assertThat(uri1).isEqualTo(uri1expected);
        assertThat(URIs.getMediaTypeAndCharset(uri1expected))
                .isEqualTo(new MediaTypeAndOrCharset(mt1.toString(), null));

        var mt2 = MediaType.PLAIN_TEXT_UTF_8;
        var uri2 = URIs.addMediaType(URI.create("scheme:something"), mt2);
        var uri2expected = URI.create("scheme:something?mediaType=text%2Fplain%3Bcharset%3Dutf-8");
        assertThat(uri2).isEqualTo(uri2expected);
        assertThat(URIs.getMediaTypeAndCharset(uri2expected))
                .isEqualTo(new MediaTypeAndOrCharset("text/plain;charset=utf-8", null));

        var uri3 = URIs.addMediaType(URI.create("scheme:?"), mt1);
        var uri3expected = URI.create("scheme:?mediaType=image%2Fgif");
        assertThat(uri3).isEqualTo(uri3expected);
    }

    @Test
    public void testAddQuery() {
        assertThat(
                        URIs.addQuery(
                                "http://host/path?arg1=a",
                                ImmutableMap.of("arg2", "b", "arg3", "c")))
                .isEqualTo("http://host/path?arg1=a&arg2=b&arg3=c");
    }

    @Test
    public void testAddCharsetQueryParameter() {
        assertThat(URIs.addCharset(URI.create("fd:1?mediaType=application/yaml"), UTF_8))
                .isEqualTo(URI.create("fd:1?mediaType=application/yaml&charset=UTF-8"));
    }

    @Test
    public void testGetCharsetFromMediaTypeOrCharsetQueryParameter() {
        assertThat(URIs.getCharset(URI.create("fd:1?mediaType=application/yaml&charset=UTF-8")))
                .isEqualTo("UTF-8");
        assertThat(URIs.getCharset(URI.create("fd:1?mediaType=application/yaml;charset=UTF-8")))
                .isEqualTo("UTF-8");
    }

    @Test
    public void testAddQueryGivenOriginalUriWithQuery() {
        var uri1 = URI.create("http://host/pathX");
        var uri2 = URI.create("http://host/pathY?arg1=a");
        var uri3 = URI.create("http://host/pathZ?arg2=b");
        var uri4 = URI.create("http://host/pathZ");

        assertThat(URIs.addQuery(uri1, uri2)).isEqualTo(URI.create("http://host/pathX?arg1=a"));
        assertThat(URIs.addQuery(uri2, uri3)).isEqualTo(uri2);
        assertThat(URIs.addQuery(uri1, uri4)).isEqualTo(uri1);
        assertThat(URIs.addQuery(uri2, uri4)).isEqualTo(uri2);
    }

    @Test
    public void testGetFilename() throws URISyntaxException {
        // Files
        assertName(URI.create(""), "");
        assertName(new File("test.txt").toURI(), "test.txt");
        assertName(new File("test.txt?p=1").toURI(), "test.txt");
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

        // No schema
        assertName(URI.create("test.txt"), "test.txt");
        assertName(URI.create(""), "");

        assertName(URI.create("whatever:/place/something.test"), "something.test");
        assertName(URI.create("whatever:place/something.test"), "something.test");
        assertName(URI.create("whatever:something.test"), "something.test");

        assertThrows(NullPointerException.class, () -> URI.create(null));
    }

    private void assertName(URI uri, String expectedFilename) {
        assertThat(URIs.getFilename(uri)).isEqualTo(expectedFilename);
    }

    @Test
    public void testGetFilenameOrLastPathSegment() {
        assertThat(URIs.getFilenameOrLastPathSegmentOrHost(URI.create("file:///tmp/")))
                .isEqualTo("tmp");

        assertThat(URIs.getFilenameOrLastPathSegmentOrHost(URI.create("https://vorburger.ch/")))
                .isEqualTo("vorburger.ch");

        assertThat(URIs.getFilenameOrLastPathSegmentOrHost(URI.create("https://vorburger.ch")))
                .isEqualTo("vorburger.ch");

        assertThat(
                        URIs.getFilenameOrLastPathSegmentOrHost(
                                URI.create("https://vorburger.ch/favicon.ico/")))
                .isEqualTo("favicon.ico");

        assertThat(
                        URIs.getFilenameOrLastPathSegmentOrHost(
                                URI.create("https://vorburger.ch/favicon.ico")))
                .isEqualTo("favicon.ico");
    }

    /** See {@link URIs#getPath(URI)} */
    @Test
    public void testGetPath() {
        var f = new File("/absolute/file?param=abc#anchor");
        // NOK! assertThat(f.toURI().getPath()).isEqualTo("/absolute/file");
        assertThat(URIs.getPath(f.toURI())).isEqualTo("/absolute/file");

        assertThat(URI.create("file:/absolute/file").getPath()).isEqualTo("/absolute/file");
        assertThat(URIs.getPath(URI.create("file:/absolute/file"))).isEqualTo("/absolute/file");

        // NOK! assertThat(URI.create("file:relative/file").getPath()).isEqualTo("relative/file");
        assertThat(URIs.getPath(URI.create("file:relative/file"))).isEqualTo("relative/file");

        assertThat(URIs.getPath(URI.create("whatever:/place/something.test")))
                .isEqualTo("/place/something.test");
        assertThat(URIs.getPath(URI.create("whatever:something.test"))).isEqualTo("something.test");

        // Glob URIs
        assertThat(URIs.getPath("file:/tmp//?.txt")).isEqualTo("/tmp//?.txt");
        assertThat(URIs.getPath("file:/tmp//{xy}?q=.")).isEqualTo("/tmp//{xy}");
    }

    @Test
    public void testGetFilePathFromURI() {
        assertThat(URIs.getFilePath(URI.create("file:/tmp/"))).isEqualTo(Path.of("/tmp"));
        assertThat(URIs.getFilePath(URI.create("file:/tmp/file"))).isEqualTo(Path.of("/tmp/file"));

        assertThat(URIs.getFilePath(URI.create("file:/tmp/file?mediaType=application/json")))
                .isEqualTo(Path.of("/tmp/file"));

        assertThat(URIs.getFilePath(URI.create("file:/tmp/file?q=p#fragment")))
                .isEqualTo(Path.of("/tmp/file"));
    }

    @Test
    public void testGetFilePathFromString() {
        assertThat(URIs.getFilePath("file:/tmp/")).isEqualTo(Path.of("/tmp"));

        // Glob URIs
        assertThat(URIs.getFilePath("file:/tmp//?.txt")).isEqualTo(Path.of("/tmp/?.txt"));
        assertThat(URIs.getFilePath("file:/tmp//{xy}?q=.")).isEqualTo(Path.of("/tmp/{xy}"));
    }

    @Test
    public void testGetScheme() {
        assertThat(URIs.getScheme("test:something")).isEqualTo("test");
        assertThat(URIs.getScheme("rela/tive")).isEqualTo("");
        assertThat(URIs.getScheme("/absolute/rela/tive")).isEqualTo("");
        assertThat(URIs.getScheme(null)).isEqualTo("");
    }

    @Test
    public void testGetSchemeSpecificPart() {
        assertThat(URIs.getSchemeSpecificPart("test:something")).isEqualTo("something");
        assertThat(URIs.getSchemeSpecificPart("rela/tive")).isEqualTo("");
        assertThat(URIs.getSchemeSpecificPart("/absolute/rela/tive")).isEqualTo("");
        assertThat(URIs.getSchemeSpecificPart(null)).isEqualTo("");
    }

    @Test
    public void testCreate() {
        // Nota bene: The / is important! At least in Java. Without it, everything that follows
        // is just one single "schemeSpecificPart"; only with it does it get broken up (in Java).
        var text = "scheme:/thing?ping=pong=pang#fragment";
        var uri = URI.create(text);
        assertThat(uri.getQuery()).isEqualTo("ping=pong=pang");
        assertThat(uri.getFragment()).isEqualTo("fragment");
        assertThat(uri.getScheme()).isEqualTo("scheme");
        assertThat(uri.getPath()).isEqualTo("/thing");
        assertThat(uri.toString()).isEqualTo(text);
    }

    @Test
    public void testPreserveEmptyFragment() {
        var text = "http://www.w3.org/2001/XMLSchema#";
        var uri = URI.create(text);
        assertThat(uri.getFragment()).isEqualTo("");
        assertThat(uri.toString()).isEqualTo(text);
    }

    @Test
    public void testDropQuery() {
        var uri = create("file:/tmp/test/picasso.yaml?context=file:test/picasso-context.jsonld");
        assertThat(URIs.dropQueryAndFragment(uri)).isEqualTo(create("file:/tmp/test/picasso.yaml"));
    }

    @Test
    public void jimURI() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Path path1 = fs.getPath("/directory/file");
            URI uri = path1.toUri();
            Path path2 = URIs.getFilePath(uri);
            assertThat(path2).isEqualTo(path1);
        }
    }

    @Test
    public void absolutifyURIs() {
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, URI.create("ascheme:///root/"))) {
            assertThat(URIs.absolutify("test")).isEqualTo("ascheme:///root/test");
            assertThat(URIs.absolutify("/test")).isEqualTo("ascheme:///test");
        }
        try (var ctx =
                TLC.open().push(URIs.ContextKeys.BASE, URI.create("ascheme://authority/root/"))) {
            assertThat(URIs.absolutify(URI.create("test")))
                    .isEqualTo(URI.create("ascheme://authority/root/test"));
            assertThat(URIs.absolutify(URI.create("/test")))
                    .isEqualTo(URI.create("ascheme://authority/test"));
        }
        assertThrows(IllegalStateException.class, () -> URIs.absolutify(URI.create("test")));
    }

    @Test
    public void absolutifyStringURIs() {
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, URI.create("ascheme:///root/"))) {
            assertThat(URIs.absolutify("test")).isEqualTo("ascheme:///root/test");
            assertThat(URIs.absolutify("/test")).isEqualTo("ascheme:///test");
        }
        try (var ctx =
                TLC.open().push(URIs.ContextKeys.BASE, URI.create("ascheme://authority/root/"))) {
            assertThat(URIs.absolutify("test")).isEqualTo("ascheme://authority/root/test");
            // TODO assertThat(URIs.absolutify("/test")).isEqualTo("ascheme://authority/test");
        }
        assertThrows(IllegalStateException.class, () -> URIs.absolutify("test"));
    }

    @Test
    public void baseOfURI() throws IOException {
        assertThat(
                        URIs.getBase(
                                URI.create(
                                        "https://www.vorburger.ch/projects/alt/index.html?q=abc#f")))
                .isEqualTo(URI.create("https://www.vorburger.ch/projects/alt"));

        assertThat(URIs.getBase(URI.create("https://www.vorburger.ch/projects/alt/")))
                .isEqualTo(URI.create("https://www.vorburger.ch/projects/alt/"));

        assertThat(URIs.getBase(URI.create("https://www.vorburger.ch/projects/alt/?q=abc#f")))
                .isEqualTo(URI.create("https://www.vorburger.ch/projects/alt/"));
    }

    @Test
    @Ignore // TODO FIXME, see class IRIs
    public void baseOfIRI() throws IOException {
        assertThat(URIs.getBase(URI.create("https://dév.dev/projects/alt/index.html?q=abc#f")))
                .isEqualTo(URI.create("https://dév.dev/projects/alt"));

        assertThat(URIs.getBase(URI.create("https://dév.dev/projects/alt/")))
                .isEqualTo(URI.create("https://dév.dev/projects/alt"));

        assertThat(URIs.getBase(URI.create("https://dév.dev/projects/alt/?q=abc#f")))
                .isEqualTo(URI.create("https://dév.dev/projects/alt"));
    }

    @Test
    @Ignore // TODO FIXME, see class IRIs
    public void baseOfIRIwithPort() throws IOException {
        assertThat(URIs.getBase(URI.create("https://dév.dev:8080/projects/alt/index.html?q=abc#f")))
                .isEqualTo(URI.create("https://dév.dev:8080/projects/alt"));

        assertThat(URIs.getBase(URI.create("https://dév.dev:8080/projects/alt/")))
                .isEqualTo(URI.create("https://dév.dev:8080/projects/alt"));

        assertThat(URIs.getBase(URI.create("https://dév.dev:8080/projects/alt/?q=abc#f")))
                .isEqualTo(URI.create("https://dév.dev:8080/projects/alt"));
    }

    @Test
    public void addFragment() {
        assertThat(URIs.addFragment(URI.create("http://example.org"), "fragment"))
                .isEqualTo(URI.create("http://example.org#fragment"));

        assertThat(URIs.addFragment(URI.create("http://example.org#foo"), "bar"))
                .isEqualTo(URI.create("http://example.org#foobar"));

        assertThat(URIs.addFragment(URI.create("http://example.org"), ""))
                .isEqualTo(URI.create("http://example.org"));
    }
}
