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

import static dev.enola.core.aspects.FilestoreRepositoryAspect.Format.YAML;

import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.aspects.FilestoreRepositoryAspect;
import dev.enola.core.aspects.TimestampAspect;
import dev.enola.core.aspects.UriTemplateAspect;
import dev.enola.core.aspects.ValidationAspect;
import dev.enola.core.meta.EntityKindRepository;

import java.nio.file.Path;

public class EnolaServiceProvider {

    public EnolaService get(EntityKindRepository ekr) throws ValidationException {
        var r = new EnolaServiceRegistry();
        for (var ek : ekr.list()) {
            var s = new EntityAspectService(ek);

            // Order here matters!
            // TODO Make the FilestoreRepositoryAspect conditional on connectors list in meta proto
            // TODO Make '.' & Format configurable, probably through a Config proto
            s.add(new FilestoreRepositoryAspect(Path.of("."), YAML));
            s.add(new UriTemplateAspect(ek));
            s.add(new TimestampAspect());
            s.add(new ValidationAspect());

            r.register(ek.getId(), s);
        }
        return r;
    }
}
