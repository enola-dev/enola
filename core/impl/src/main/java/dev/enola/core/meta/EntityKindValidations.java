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
package dev.enola.core.meta;

import com.google.common.base.Strings;

import dev.enola.common.protobuf.MessageValidator;
import dev.enola.common.protobuf.MessageValidators;
import dev.enola.core.proto.ID;

public class EntityKindValidations {

    private static final MessageValidator<Void, ID> id =
            (ctx, m, r) -> {
                // TODO Validate that NS matches regexp as per enola_core.proto

                if (Strings.isNullOrEmpty(m.getEntity())) {
                    // TODO Simplify making fields required
                    r.add(ID.getDescriptor(), "mandatory");
                } else {
                    // TODO Validate that entity name matches regexp as per enola_core.proto
                }

                // TODO Validate that paths all match regexp as per enola_core.proto
            };

    public static final MessageValidators INSTANCE =
            new MessageValidators().register(id, ID.getDescriptor());
}
