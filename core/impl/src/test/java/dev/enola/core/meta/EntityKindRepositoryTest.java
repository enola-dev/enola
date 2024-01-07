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
package dev.enola.core.meta;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.FileWriteMode.APPEND;
import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.IDs;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;

@SuppressWarnings("IgnoredPureGetter") // https://github.com/enola-dev/enola/issues/224
public class EntityKindRepositoryTest {

    EntityKindRepository r = new EntityKindRepository();

    @Test
    public void testEmptyRepository() throws ValidationException {
        // Even an empty repository always has the built-in enola.entity_kind
        assertThat(r.list()).hasSize(2);
        assertThat(r.listID()).hasSize(2);

        var id = ID.newBuilder().setEntity("non-existant").build();
        assertThrows(IllegalArgumentException.class, () -> r.get(id));
    }

    @Test
    public void testBasics() throws ValidationException {
        var id = ID.newBuilder().setNs("test").setEntity("test").build();
        var ek = EntityKind.newBuilder().setId(id).build();

        r.put(ek);
        assertThat(r.get(id)).isEqualTo(ek);
        assertThat(r.listID()).contains(id);
        assertThat(r.list()).contains(ek);
    }

    @Test
    public void testEmptyNamespace() throws ValidationException {
        var id = ID.newBuilder().setEntity("test").build();
        var ek = EntityKind.newBuilder().setId(id).build();

        r.put(ek);
        assertThat(r.get(id)).isEqualTo(ek);
        assertThat(r.listID()).contains(id);
        assertThat(r.list()).contains(ek);
    }

    @Test
    public void testEmptyName() {
        var id = ID.newBuilder().build();
        var ek = EntityKind.newBuilder().setId(id).build();

        assertThrows(IllegalArgumentException.class, () -> r.get(id));
        assertThrows(ValidationException.class, () -> r.put(ek));
    }

    @Test
    public void testLoadTextproto() throws ValidationException, IOException {
        r.load(new ClasspathResource("demo-model.textproto"));
        assertThat(r.listID())
                .containsAtLeast(
                        IDs.parse("demo.foo/name"),
                        IDs.parse("demo.bar/foo/name"),
                        IDs.parse("demo.baz/uuid"));
        // The 4th one is the built-in enola.entity_kind
        assertThat(r.list()).hasSize(5);
    }

    @Test
    public void testLoadYAML() throws ValidationException, IOException {
        r.load(new ClasspathResource("demo-model.yaml"));
        assertThat(r.listID())
                .containsAtLeast(IDs.parse("demo.foo/name"), IDs.parse("demo.bar/foo/name"));
        // The 3rd one is the built-in enola.entity_kind
        assertThat(r.list()).hasSize(4);
    }

    @Test
    public void testReLoad() throws ValidationException, IOException {
        var cpr = new ClasspathResource("demo-model.yaml");
        var temp = Files.createTempFile("EntityKindRepositoryTest-", "-testReLoad.yaml");
        var fr = new FileResource(temp, cpr.mediaType().charset().get());
        try {
            cpr.byteSource().copyTo(fr.byteSink());
            r.load(fr);

            var id = ID.newBuilder().setNs("demo").setEntity("bar").build();
            var ek = r.get(id);
            ek.getLinkOrThrow("wiki");

            com.google.common.io.Files.asCharSink(temp.toFile(), UTF_8, APPEND)
                    .write("      newone:\n" + "        label: newone\n");
            ek = r.get(id);
            ek.getLinkOrThrow("newone");

        } finally {
            temp.toFile().delete();
        }
    }
}
