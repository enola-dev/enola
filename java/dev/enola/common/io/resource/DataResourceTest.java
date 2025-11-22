/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import static com.google.common.net.MediaType.JSON_UTF_8;
import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;
import static com.google.common.truth.Truth.assertThat;

import static java.nio.charset.StandardCharsets.US_ASCII;

import com.google.common.net.MediaType;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class DataResourceTest {

    MediaType PLAIN_TEXT_ASCII = PLAIN_TEXT_UTF_8.withCharset(US_ASCII);

    private void check(String data, MediaType mediaType, byte[] bytes) throws IOException {
        check(new DataResource(URI.create("data:" + data)), mediaType, bytes);
    }

    private void check(Resource resource, MediaType mediaType, byte[] bytes) throws IOException {
        assertThat(resource.mediaType()).isEqualTo(mediaType);
        assertThat(resource.byteSource().read()).isEqualTo(bytes);
    }

    @Test
    public void valid() throws IOException {
        check(",", PLAIN_TEXT_ASCII, "".getBytes(US_ASCII));
        check(",hello", PLAIN_TEXT_ASCII, "hello".getBytes(US_ASCII));
        check(",hello%20world", PLAIN_TEXT_ASCII, "hello world".getBytes(US_ASCII));
        check(
                "text/plain;charset=UTF-8;page=21,the%20data:1234,5678",
                PLAIN_TEXT_UTF_8.withParameter("page", "21"),
                "the data:1234,5678".getBytes(US_ASCII));
        check(
                "application/json;charset=UTF-8,%7B%22key%22%3A+%22value%22%7D",
                JSON_UTF_8, "{\"key\": \"value\"}".getBytes(US_ASCII));
        check(
                "image/jpeg;x=y;base64,/9j/4AAQSkZJRgABAgAAZABkAAD",
                MediaType.JPEG.withParameter("x", "y"),
                new byte[] {
                    -1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 2, 0, 0, 100, 0, 100, 0, 0
                });
        check(";base64,SGVsbG8gV29ybGQh", PLAIN_TEXT_ASCII, "Hello World!".getBytes(US_ASCII));
    }

    @Test
    public void ofString() throws IOException {
        check(DataResource.of(null), PLAIN_TEXT_ASCII, new byte[] {});
        check(DataResource.of(""), PLAIN_TEXT_ASCII, new byte[] {});
        check(DataResource.of("hello"), PLAIN_TEXT_ASCII, "hello".getBytes(US_ASCII));
        check(DataResource.of("hello, world"), PLAIN_TEXT_ASCII, "hello, world".getBytes(US_ASCII));
        check(DataResource.of("hello+ world"), PLAIN_TEXT_ASCII, "hello+ world".getBytes(US_ASCII));

        check(DataResource.of("{ }", JSON_UTF_8), JSON_UTF_8, "{ }".getBytes(US_ASCII));

        var mt2 = JSON_UTF_8.withParameter("page", "21").withParameter("x", "y");
        check(DataResource.of("{ }", mt2), mt2, "{ }".getBytes(US_ASCII));
    }

    @Test(expected = IllegalArgumentException.class)
    public void empty() {
        new DataResource(URI.create(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void spaceIsInvalid() {
        new DataResource(URI.create("data:hello world"));
    }

    @Test
    public void dataURIWithEmptyMediaTypePartDefaultsToTextPlain() throws IOException {
        var resource1 = DataResource.of("hello, world");
        var uri = resource1.uri();
        var resource2 = new DataResource(uri);
        assertThat(resource2.charSource().read()).isEqualTo("hello, world");
        assertThat(resource2.mediaType()).isEqualTo(PLAIN_TEXT_ASCII);
    }

    @Test
    public void dataURIWithMediaTypeAndNoParameters() throws IOException {
        var uri = URI.create("data:text/plain,hi");
        var resource = new DataResource(uri);
        assertThat(resource.byteSource().read()).isEqualTo("hi".getBytes(US_ASCII));
        assertThat(resource.mediaType()).isEqualTo(MediaType.parse("text/plain"));
    }
}
