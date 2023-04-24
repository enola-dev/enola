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
package dev.enola.core;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.meta.proto.Link;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;

import org.junit.Test;

public class EntityServiceProviderTest {

    @Test
    public void testEnolaServiceProvider() throws ValidationException, EnolaException {
        var template = "https://www.google.com/search?q={path.name}+dog&sclient=img";
        var kid = ID.newBuilder().setNs("test").setEntity("dog").addPaths("name").build();
        var href = Link.newBuilder().setUriTemplate(template).build();
        var kind = EntityKind.newBuilder().setId(kid).putLink("image", href).build();
        var ekr = new EntityKindRepository().put(kind);
        var service = new EnolaServiceProvider().get(ekr);

        var eid = ID.newBuilder(kid).clearPaths().addPaths("kind-charles").build();
        var request = GetEntityRequest.newBuilder().setId(eid).build();
        var response = service.getEntity(request);
        var entity = response.getEntity();

        assertThat(entity.getId()).isEqualTo(eid);

        assertThat(entity.getLinkMap().get("image"))
                .isEqualTo("https://www.google.com/search?q=kind-charles+dog&sclient=img");

        assertThat(entity.getTs().getSeconds()).isGreaterThan(123);

        // TODO test File Store Aspect
    }
}
