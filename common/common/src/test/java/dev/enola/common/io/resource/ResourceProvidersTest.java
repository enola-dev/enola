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

import static org.junit.Assert.assertThrows;

import com.google.common.io.Files;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class ResourceProvidersTest {

    private static final byte[] BYTES = new byte[] {1, 2, 3};

    private void check(Class<?> expected, URI uri) {
        assertThat(new ResourceProviders().getResource(uri)).isInstanceOf(expected);
    }

    @Test
    public void testNull() {
        check(NullResource.class, NullResource.INSTANCE.uri());
    }

    @Test
    public void testEmpty() throws IOException {
        var r = new ResourceProviders().getResource(URI.create("file:///dev/null"));
        assertThat(r.byteSource().isEmpty()).isTrue();
    }

    private void checkFile(File file, URI uri) throws IOException {
        check(FileResource.class, uri);
        Files.asByteSink(file).write(BYTES);
        byte[] bytes = new ResourceProviders().getResource(uri).byteSource().read();
        assertThat(bytes).isEqualTo(BYTES);
    }

    @Test
    public void testFile() throws IOException {
        check(FileResource.class, URI.create("file:///dev/null"));

        var r = new ResourceProviders().getResource(URI.create("file:///dev/null"));
        assertThat(r.byteSource().isEmpty()).isTrue();

        var f = new File("absolute").getAbsoluteFile();
        checkFile(f, f.toURI());
    }

    @Test
    public void testRelativeFile() throws IOException {
        checkFile(new File("relative"), URI.create("file:relative"));

        var f = new File("folder/relative");
        f.getParentFile().mkdir();
        checkFile(f, URI.create("file:folder/relative"));
    }

    @Test
    @Ignore // TODO Implement me, with a local test HTTP server...
    public void testHTTP() {}

    @Test
    public void testString() throws IOException {
        var uri = URI.create(StringResource.SCHEME + ":hello");
        assertThat(new ResourceProviders().getReadableResource(uri).charSource().read())
                .isEqualTo("hello");
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
