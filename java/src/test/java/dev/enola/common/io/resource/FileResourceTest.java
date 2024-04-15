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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.net.MediaType;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class FileResourceTest {

    @Test
    public void testWriteRead() throws IOException {
        var t = Files.createTempFile("FileResourceTest", ".json").toAbsolutePath();
        var r = new FileResource(t.toUri());
        assertThat(r.uri().toString()).endsWith(".json");
        assertThat(r.mediaType()).isEqualTo(MediaType.JSON_UTF_8);
        check(r);
    }

    @Test
    public void testWriteFileInNonExistingDirectory() throws IOException {
        var tmp = System.getProperty("java.io.tmpdir");
        var dir = new File(tmp, "FileResourceTest-" + Long.toString(System.nanoTime()));
        assertThat(dir.exists()).isFalse();
        var file = new File(dir, "text.txt");
        var r = new FileResource(file.toURI(), MediaType.PLAIN_TEXT_UTF_8);
        r.charSink().write("hello, world");
        assertThat(file.delete()).isTrue();
        assertThat(dir.delete()).isTrue();
    }

    @Test(expected = NoSuchFileException.class)
    public void readNonExisting() throws IOException {
        var r = new FileResource(URI.create("file:does-not-exist.txt"), MediaType.PLAIN_TEXT_UTF_8);
        r.charSource().read();
    }

    @Test // https://github.com/google/jimfs
    public void testBasicJimFS() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path foo = fs.getPath("/testBasicJimFS");
        Path hello = foo.resolve("hello.txt"); // /foo/hello.txt

        var r = new FileResource(hello.toUri(), MediaType.PLAIN_TEXT_UTF_8);
        check(r);
    }

    @Test // https://github.com/google/jimfs
    public void testPathToURIonJimFSwithNewName() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path foo = fs.getPath("/testPathToURIonJimFSwithNewName");

        var uriString = foo.toUri().toString() + "/hello.txt";
        assertThat(uriString).startsWith("jimfs://");
        assertThat(uriString).endsWith("/hello.txt");
        var uri = URI.create(uriString);

        // Nota Bene: Do *NOT* use Path here, only URI! Path loses the jimfs: scheme!

        var r = new FileResource(uri, MediaType.PLAIN_TEXT_UTF_8);
        check(r);

        // To make sure it didn't just work because it was something like:
        // file:///tmp/bazel-working-directory/_main/bazel-out/k8-fastbuild/bin/common/common/src/test/java/dev/enola/common/io/resource/FileResourceTest.runfiles/_main/jimfs:/dc113772-abe6-4ffd-be54-eeaecdc34414/testPathToURIonJimJS/hello.txt
        // let's double-check:
        assertThat(r.uri().getScheme()).isEqualTo("jimfs");
        assertThat(Files.readString(r.path())).isEqualTo("hello, world");
    }

    private void check(Resource r) throws IOException {
        r.charSink().write("hello, world");
        assertThat(r.charSource().read()).isEqualTo("hello, world");
    }
}
