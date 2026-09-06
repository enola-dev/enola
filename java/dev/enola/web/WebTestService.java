/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.web;

import com.google.protobuf.Any;

import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.GetThingsRequest;
import dev.enola.core.proto.GetThingsResponse;
import dev.enola.thing.proto.Thing;

class WebTestService implements EnolaService {

    Thing thing = Thing.newBuilder().setIri("http://example.org/test").build();

    @Override
    public GetThingsResponse getThings(GetThingsRequest r) throws EnolaException {
        return GetThingsResponse.newBuilder().addThings(thing).build();
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        return GetThingResponse.newBuilder().setThing(Any.pack(thing)).build();
    }
}
