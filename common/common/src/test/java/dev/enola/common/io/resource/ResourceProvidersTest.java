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

import static org.junit.Assert.assertThrows;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.YamlMediaType;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ResourceProvidersTest {

    private static final byte[] BYTES = new byte[] {1, 2, 3};

    private void check(Class<?> expected, URI uri) {
        assertThat(new ResourceProviders().getResource(uri)).isInstanceOf(expected);
    }

    @Test
    public void testNull() throws IOException {
        var uri = NullResource.INSTANCE.uri();
        check(NullResource.class, uri);
        new ResourceProviders().getResource(uri).charSink().write("hi");
    }

    @Test
    public void testEmpty() throws IOException {
        // NB: URI.create("empty:") causes an java.net.URISyntaxException, so:
        var uri = URI.create("empty:?");
        var r = new ResourceProviders().getResource(uri);
        assertThat(r.byteSource().isEmpty()).isTrue();
        assertThat(r.mediaType()).isEqualTo(MediaType.OCTET_STREAM);

        uri = URI.create("empty:?mediaType=" + MediaType.JSON_UTF_8.toString().replace(" ", ""));
        r = new ResourceProviders().getResource(uri);
        assertThat(r.byteSource().isEmpty()).isTrue();
        assertThat(r.mediaType()).isEqualTo(MediaType.JSON_UTF_8);
    }

    @Test
    public void testFileMediaType() {
        Resource r;
        var rp = new ResourceProviders();

        r = rp.getResource(URI.create("file:test.yaml"));
        assertThat(r.mediaType()).isEqualTo(YamlMediaType.YAML_UTF_8);

        r = rp.getResource(URI.create("file:yamlisjson.yaml?mediaType=application/json"));
        assertThat(r.mediaType()).isEqualTo(MediaType.JSON_UTF_8);

        r = rp.getResource(URI.create("file:noextension?mediaType=application/json"));
        assertThat(r.mediaType()).isEqualTo(MediaType.JSON_UTF_8);

        r = rp.getResource(URI.create("file:noextension?charset=UTF-16BE"));
        assertThat(r.mediaType())
                .isEqualTo(MediaType.OCTET_STREAM.withCharset(StandardCharsets.UTF_16BE));

        r = rp.getResource(URI.create("file:test.json?charset=UTF-16BE"));
        assertThat(r.mediaType()).isEqualTo(MediaType.JSON_UTF_8.withCharset(Charsets.UTF_16BE));
    }

    private void checkReadFile(File file, URI uri) throws IOException {
        check(FileResource.class, uri);
        Files.asByteSink(file).write(BYTES);
        byte[] bytes = new ResourceProviders().getResource(uri).byteSource().read();
        assertThat(bytes).isEqualTo(BYTES);
    }

    @Test
    public void testReadAbsoluteFile() throws IOException {
        check(FileResource.class, URI.create("file:///dev/null"));

        var r = new ResourceProviders().getResource(URI.create("file:///dev/null"));
        assertThat(r.byteSource().isEmpty()).isTrue();

        var f = new File("absolute").getAbsoluteFile();
        checkReadFile(f, f.toURI());
    }

    @Test
    public void testReadRelativeFile() throws IOException {
        checkReadFile(new File("relative"), URI.create("file:relative"));

        var f = new File("folder/relative");
        f.getParentFile().mkdir();
        checkReadFile(f, URI.create("file:folder/relative"));
    }

    private void checkWriteFile(File f, Charset cs, URI uri) throws IOException {
        var hello = "hello, world";

        var mediaType = YamlMediaType.YAML_UTF_8.withCharset(cs);
        var uriWithMT = URIs.addMediaType(uri, mediaType);
        var resource = new ResourceProviders().getWritableResource(uriWithMT);
        assertThat(resource.mediaType()).isEqualTo(mediaType);

        resource.charSink().write(hello);
        String text = Files.asCharSource(f, cs).read();
        assertThat(text).isEqualTo(hello);
    }

    @Test
    public void testWriteAbsoluteFileMediaTypeEncoding() throws IOException {
        var cs = StandardCharsets.UTF_16LE;
        var f = new File("testWriteFileMediaTypeEncoding.txt").getAbsoluteFile();
        checkWriteFile(f, cs, f.toURI());
    }

    @Test
    public void testWriteRelativeFileMediaTypeEncoding() throws IOException {
        var cs = StandardCharsets.UTF_16LE;
        var f = new File("testWriteFileMediaTypeEncoding.txt");
        checkWriteFile(f, cs, f.toURI());
        checkWriteFile(f, cs, URI.create("file:relative"));
    }

    @Test
    @Ignore // TODO Implement me, with a local test HTTP server...
    public void testHTTP() {}

    @Test
    public void testClasspath() throws IOException {
        var uri = URI.create(ClasspathResource.SCHEME + ":test-emoji.txt");
        var emoji = new ResourceProviders().getReadableResource(uri).charSource().read();
        assertThat(emoji).isEqualTo("🕵🏾‍♀️\n");
    }

    @Test
    public void testString() throws IOException {
        var rp = new ResourceProviders();

        var uri = URI.create(StringResource.SCHEME + ":hello");
        var hello = rp.getReadableResource(uri).charSource().read();
        assertThat(hello).isEqualTo("hello");

        // StringResource (intentionally) does not support ?mediaType= nor (need) #anchor
        uri = URI.create("string:%23%20Models%0A"); // "# Models\n"
        var docDefaultGenHeader = rp.getReadableResource(uri).charSource().read();
        assertThat(docDefaultGenHeader).isEqualTo("# Models\n");

        // NB: Empty strings cannot work; URI.create("string:") causes java.net.URISyntaxException.
    }

    @Test
    public void testSTDOUT() throws IOException {
        new ResourceProviders()
                .getResource(URI.create("fd:1?charset=UTF-8"))
                .charSink()
                .write("hi");
    }

    @Test
    public void testMemory() throws IOException {
        // Direct
        try (var r = TestResource.create(MediaType.PLAIN_TEXT_UTF_8)) {
            checkMemory(r);
        }

        // Indirect via factory and with fancy URI with ?mediaType= parameter
        try (var r1 = TestResource.create(MediaType.JSON_UTF_8)) {
            var r2 = new ResourceProviders().getResource(URI.create(r1.uri().toString()));
            assertThat(r2.mediaType()).isEqualTo(MediaType.JSON_UTF_8);
            checkMemory(r2);
        }
    }

    private void checkMemory(Resource r) throws IOException {
        r.charSink().write("hi");
        var uri = r.uri();
        var text = new ResourceProviders().getResource(uri).charSource().read();
        assertThat(text).isEqualTo("hi");
    }

    @Test
    public void testError() {
        check(ErrorResource.class, ErrorResource.INSTANCE.uri());
    }

    @Test
    public void testNoScheme() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ResourceProviders().getResource(URI.create("test")));
    }

    @Test
    public void testUnknownScheme() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ResourceProviders().getResource(URI.create("xyz-unknown:test")));
    }
}
