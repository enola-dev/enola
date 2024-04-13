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

import com.google.common.net.MediaType;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class FileResourceTest {

    @Test
    public void testWriteRead() throws IOException {
        var t = Files.createTempFile("FileResourceTest", ".json").toAbsolutePath();
        var r = new FileResource(t);
        assertThat(r.uri().toString()).endsWith(".json");
        assertThat(r.mediaType()).isEqualTo(MediaType.JSON_UTF_8);
        r.charSink().write("hello, world");
        assertThat(r.charSource().read()).isEqualTo("hello, world");
    }

    @Test(expected = NoSuchFileException.class)
    public void readNonExisting() throws IOException {
        var r = new FileResource(Path.of("does-not-exist.txt"), MediaType.PLAIN_TEXT_UTF_8);
        r.charSource().read();
    }
}
