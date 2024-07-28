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
package dev.enola.model.enola.files;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.base.Charsets;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.net.MediaType;

import dev.enola.common.io.resource.*;
import dev.enola.thing.Thing;
import dev.enola.thing.io.ResourceIntoThingConverters;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

public class FileThingConverterTest {

    @Test
    public void jimFS() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            var root = fs.getPath("/root");
            check(root);
        }
    }

    @Test
    public void realTemp() throws IOException {
        var root = Files.createTempDirectory("FileThingConverterTest");
        check(root);
    }

    @Test // classpath:
    public void skipClasspath() throws IOException {
        var rp = new ResourceProviders(new ClasspathResource.Provider());
        var resource = rp.getResource(URI.create("classpath:/test.png"));
        var converter = new FileThingConverter();
        var list = converter.convert(resource);
        assertThat(list).isEmpty();
    }

    @Test // jar:file:
    public void skipJarFile() throws IOException {
        var converter = new FileThingConverter();
        var list = converter.convert(new ClasspathResource("test.png"));
        assertThat(list).isEmpty();
    }

    private void check(Path root) throws IOException {
        // Setup
        var cs = Charsets.UTF_8;
        var folder = root.resolve("folder");
        var hello = folder.resolve("hello.txt");
        Files.createDirectories(folder);
        Files.writeString(hello, "hello, world", cs);

        var link = folder.resolve("symlink");
        Files.createSymbolicLink(link, hello);

        // Convert
        var mt = MediaType.PLAIN_TEXT_UTF_8;
        var ritc = new ResourceIntoThingConverters(); // includes FileThingConverter
        List<Thing.Builder> list = ritc.convert(new FileResource(hello.toUri(), mt));
        var thing = list.get(0).build();

        // Assert expected Thing
        URI iri = URI.create(thing.iri());
        assertThat(iri.toString()).endsWith("/hello.txt");
        assertThat(iri.getAuthority()).isNotEmpty();
        assertThat(iri.getHost()).isNotEmpty();

        assertThat(thing.getString(File.mediaType_IRI)).isEqualTo(mt.toString());
        assertThat((Long) thing.get(File.size_IRI)).isEqualTo(12L);
        assertThat(thing.get(File.createdAt_IRI, FileTime.class)).isNotNull();
        assertThat(thing.get(File.modifiedAt_IRI, FileTime.class)).isNotNull();
    }

    // TODO Read folder, and check for 3 Things (Directory folder, File hello, Link)
}
