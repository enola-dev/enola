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
package dev.enola.core.meta;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.Repository;
import dev.enola.core.meta.proto.Type;
import dev.enola.core.meta.proto.Types;
import dev.enola.core.type.TypeRepositoryBuilder;

import org.junit.Test;

import java.io.IOException;

public class TypeRepositoryTest {

    @Test
    public void loadBaseYAML() throws IOException {
        var types = Types.newBuilder();
        var resource = new ClasspathResource("test-types.yaml");
        new ProtoIO().read(resource, types, Types.class);

        var trb = new TypeRepositoryBuilder();
        trb.addAllTypes(types.getTypesBuilderList());
        Repository<Type> tyr = trb.build();
        assertThat(tyr.getByName("enola.dev/test1")).isNotNull();
        assertThat(tyr.list()).hasSize(2);
    }

    @Test
    public void noDuplicates() {
        var trb = new TypeRepositoryBuilder();
        var type = Type.newBuilder().setName("enola.dev/testDupe").setUri("enola.dev/testDupe");
        trb.add(type);
        assertThrows(IllegalArgumentException.class, () -> assertThat(trb.add(type).build()));
    }

    @Test
    public void noName() {
        var trb = new TypeRepositoryBuilder();
        var type = Type.newBuilder();
        assertThrows(IllegalArgumentException.class, () -> assertThat(trb.add(type).build()));
    }

    @Test
    public void noURI() {
        var trb = new TypeRepositoryBuilder();
        var type = Type.newBuilder().setName("enola.dev/testDupe");
        assertThrows(IllegalArgumentException.class, () -> assertThat(trb.add(type).build()));
    }
}
