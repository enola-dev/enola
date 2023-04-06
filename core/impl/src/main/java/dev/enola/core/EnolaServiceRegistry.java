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

import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.GetEntityResponse;
import dev.enola.core.proto.ID;

import java.util.HashMap;
import java.util.Map;

class EnolaServiceRegistry implements EnolaService {

    private final Map<ID, EnolaService> registry = new HashMap<>();

    public void register(ID id, EnolaService service) {
        var lookup = IDs.withoutPath(id);
        var existing = registry.get(lookup);
        if (existing != null) {
            throw new IllegalArgumentException("Service already registered for: " + lookup);
        }
        registry.put(lookup, service);
    }

    @Override
    public GetEntityResponse getEntity(GetEntityRequest r) {
        var lookup = IDs.withoutPath(r.getId());
        var delegate = registry.get(lookup);
        if (delegate == null) {
            throw new IllegalArgumentException("No Connector registered for: " + lookup);
        }
        return delegate.getEntity(r);
    }
}
