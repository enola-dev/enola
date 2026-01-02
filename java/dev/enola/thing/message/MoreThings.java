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
package dev.enola.thing.message;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;

import java.util.List;

public final class MoreThings {

    // TODO This should eventually no longer be needed...
    public static List<Thing> fromAny(Any any) throws InvalidProtocolBufferException {
        if (any.getTypeUrl().endsWith("Thing")) {
            return List.of(any.unpack(Thing.class));
        } else if (any.getTypeUrl().endsWith("Things")) {
            var things = any.unpack(Things.class);
            return things.getThingsList();
        } else {
            return List.of();
        }
    }

    private MoreThings() {}
}
