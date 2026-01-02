/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.net.MediaType;
import com.google.common.primitives.UnsignedLong;

import dev.enola.common.context.TLC;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.*;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.UriIntoThingConverters;
import dev.enola.thing.java.TBF;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public class FileThingConverterTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set());

    private final ResourceProvider rp = new ClasspathResource.Provider();

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
        var resource = rp.getResource(URI.create("classpath:/test.png"));
        var converter = new FileThingConverter();
        var store = new ThingMemoryRepositoryROBuilder();
        assertThat(converter.convertInto(resource.uri(), store)).isFalse();
        assertThat(store.listIRI()).isEmpty();
    }

    @Test // jar:file:
    public void skipJarFile() throws IOException {
        var converter = new FileThingConverter();
        var store = new ThingMemoryRepositoryROBuilder();
        assertThat(converter.convertInto(new ClasspathResource("test.png").uri(), store)).isFalse();
        assertThat(store.listIRI()).isEmpty();
    }

    private void check(Path root) throws IOException {
        // Setup
        var cs = StandardCharsets.UTF_8;
        var folder = root.resolve("folder");
        var hello = folder.resolve("hello.txt");
        Files.createDirectories(folder);
        Files.writeString(hello, "hello, world", cs);

        var link = folder.resolve("symlink");
        Files.createSymbolicLink(link, hello);

        // Convert hello.txt
        var mt = MediaType.PLAIN_TEXT_UTF_8;
        var ritc = new UriIntoThingConverters(new FileThingConverter());

        try (var ctx = TLC.open().push(TBF.class, ImmutableThing.FACTORY)) {
            var store = new ThingMemoryRepositoryROBuilder();
            ritc.convertIntoOrThrow(hello.toUri(), store);
            var thing = store.list().iterator().next();

            // Assert expected Thing
            URI iri = URI.create(thing.iri());
            assertThat(iri.toString()).endsWith("/hello.txt");
            // TODO assertThat(iri.getAuthority()).isNotEmpty();
            // TODO assertThat(iri.getHost()).isNotEmpty();

            // assertThat(thing.getString(File.mediaType_IRI)).isEqualTo(mt.toString());
            assertThat(thing.get(File.size_IRI, UnsignedLong.class))
                    .isEqualTo(UnsignedLong.valueOf(12));
            assertThat(thing.get(File.createdAt_IRI, FileTime.class)).isNotNull();
            assertThat(thing.get(File.modifiedAt_IRI, FileTime.class)).isNotNull();

            // Convert folder
            store = new ThingMemoryRepositoryROBuilder();
            ritc.convertIntoOrThrow(folder.toUri(), store);
            thing = store.list().iterator().next();
            iri = URI.create(thing.iri());
            assertThat(iri.toString()).endsWith("/folder/");
        }
    }

    // TODO Read folder, and check for 3 Things (Directory folder, File hello, Link)
}
