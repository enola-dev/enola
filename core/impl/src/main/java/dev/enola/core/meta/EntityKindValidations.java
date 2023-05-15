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

import static dev.enola.core.IDs.toPath;

import com.google.common.base.Strings;

import dev.enola.common.protobuf.MessageValidator;
import dev.enola.common.protobuf.MessageValidators;
import dev.enola.core.IDs;
import dev.enola.core.meta.proto.EntityKinds;
import dev.enola.core.meta.proto.EntityKindsOrBuilder;
import dev.enola.core.proto.ID;

import java.util.regex.Pattern;

public class EntityKindValidations {

    // Keep the following regular expressions in-sync with the doc on
    // core/lib/src/main/java/dev/enola/core/enola_core.proto
    // AKA https://docs.enola.dev/dev/proto/core/#id
    private static final Pattern ID_NS_PATTERN = Pattern.compile("[a-z0-9_\\.]*");
    private static final Pattern ID_ENTITY_PATTERN = Pattern.compile("[a-z0-9_]+");
    private static final Pattern ID_PATHS_PATTERN = Pattern.compile("[a-z0-9_\\-\\.]+");

    private static void check(
            String text, Pattern pattern, String what, ID id, MessageValidators.Result.Builder r) {
        if (!pattern.matcher(text).matches()) {
            r.add(
                    ID.getDescriptor(),
                    IDs.toPath(id)
                            + " "
                            + what
                            + "="
                            + text
                            + " does not match valid RegExp "
                            + pattern.pattern());
        }
    }

    public static final MessageValidator<EntityKindRepository, EntityKindsOrBuilder> eksv =
            (ekr, eks, r) -> {
                for (var ek : eks.getKindsList()) {
                    for (var er : ek.getRelatedMap().entrySet()) {
                        var key = er.getKey();
                        var id = er.getValue().getId();
                        if (id == null) continue;
                        if (ekr.getOptional(IDs.withoutPath(id)).isEmpty()) {
                            var msg = toPath(ek.getId()) + "#related." + key + " = " + id;
                            r.add(ID.getDescriptor(), msg);
                        }
                    }
                }
            };

    private static final MessageValidator<Void, ID> idv =
            (ctx, id, r) -> {
                check(id.getNs(), ID_NS_PATTERN, "ns", id, r);

                if (Strings.isNullOrEmpty(id.getEntity())) {
                    // TODO Simplify making fields required
                    r.add(ID.getDescriptor(), "mandatory");
                } else {
                    check(id.getEntity(), ID_ENTITY_PATTERN, "entity", id, r);
                }

                for (String path : id.getPathsList()) {
                    check(path, ID_PATHS_PATTERN, "path", id, r);
                }
            };

    public static final MessageValidators INSTANCE =
            new MessageValidators()
                    .register(idv, ID.getDescriptor())
                    .register(eksv, EntityKinds.getDescriptor());
}
