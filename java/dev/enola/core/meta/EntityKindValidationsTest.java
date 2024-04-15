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

import static org.junit.Assert.assertThrows;

import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.meta.proto.EntityRelationship;
import dev.enola.core.proto.ID;

import org.junit.Test;

public class EntityKindValidationsTest {

    @Test
    public void validateEntityKindWithBrokenRelated() {
        var ek1 = EntityKind.newBuilder();
        var ek1id = ID.newBuilder().setNs("test").setEntity("test");
        var ek1rel1id = ID.newBuilder().setNs("test").setEntity("bad");
        var ek1rel1 = EntityRelationship.newBuilder().setId(ek1rel1id);
        ek1.putRelated("rel1", ek1rel1.build());
        ek1.setId(ek1id);

        var ekr = new EntityKindRepository();
        assertThrows(ValidationException.class, () -> ekr.put(ek1.build()));
    }

    @Test
    public void validateIDs() throws ValidationException {
        // Invalid NS
        checkInvalidID(ID.newBuilder().setNs("n#k").setEntity("ok").build());
        checkInvalidID(ID.newBuilder().setNs("NOK").setEntity("ok").build());
        checkInvalidID(ID.newBuilder().setNs("näk").setEntity("ok").build());

        // Invalid Entity
        checkInvalidID(ID.newBuilder().setNs("ok").setEntity("NOK").build());
        checkInvalidID(ID.newBuilder().setNs("ok").setEntity("näk").build());
        checkInvalidID(ID.newBuilder().setNs("ok").setEntity("n#k").build());

        // Invalid Paths
        checkInvalidID(ID.newBuilder().setNs("ok").setEntity("ok").addPaths("NOK").build());
        checkInvalidID(ID.newBuilder().setNs("ok").setEntity("ok").addPaths("näk").build());
        checkInvalidID(ID.newBuilder().setNs("ok").setEntity("ok").addPaths("n#k").build());
    }

    private void checkInvalidID(ID id) {
        var ek1 = EntityKind.newBuilder();
        ek1.setId(id);
        var ekr = new EntityKindRepository();
        assertThrows(ValidationException.class, () -> ekr.put(ek1.build()));
    }
}
