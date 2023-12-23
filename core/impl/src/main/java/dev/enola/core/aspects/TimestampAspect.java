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
package dev.enola.core.aspects;

import dev.enola.common.protobuf.Timestamps2;
import dev.enola.core.EnolaException;
import dev.enola.core.EntityAspectRepeater;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;

import java.time.Clock;
import java.time.Instant;

public class TimestampAspect implements EntityAspectRepeater {
    private final Clock clock = Clock.systemUTC();

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException {
        // If a previously processed ("Connector") aspect already set a TS, we do not overwrite it!
        if (entity.getTsOrBuilder().getSeconds() == 0) {
            var now = Timestamps2.fromInstant(Instant.now(clock));
            entity.setTs(now);
        }
    }
}
