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

import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.aspects.TimestampAspect;
import dev.enola.core.aspects.UriTemplateAspect;
import dev.enola.core.meta.EntityKindRepository;

public class EnolaServiceProvider {

    public EnolaService get(EntityKindRepository ekr) throws ValidationException {
        var r = new EnolaServiceRegistry();
        for (var ek : ekr.list()) {
            var s = new EntityAspectService(ek);

            // TODO s.add(fileStoreAspect);
            s.add(new UriTemplateAspect(ek));
            s.add(new TimestampAspect());

            r.register(ek.getId(), s);
        }
        return r;
    }
}
