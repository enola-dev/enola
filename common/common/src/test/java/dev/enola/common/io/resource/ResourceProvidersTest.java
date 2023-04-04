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

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class ResourceProvidersTest {

    private void check(Class expected, URI uri) {
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

    @Test
    public void testFile() throws IOException {
        check(FileResource.class, URI.create("file:///dev/null"));

        var r = new ResourceProviders().getResource(URI.create("file:///dev/null"));
        assertThat(r.byteSource().isEmpty()).isTrue();
    }

    @Test
    @Ignore // TODO Implement me ASAP!
    public void testNoSchemeIsRelativeFile() {}

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
    public void testUnknownScheme() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ResourceProviders().getResource(URI.create("xyz-unknown:test")));
    }
}
