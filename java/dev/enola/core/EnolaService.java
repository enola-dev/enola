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
package dev.enola.core;

import dev.enola.core.proto.*;

public interface EnolaService {
    // TODO Merge this "local" API with the ("remote") dev.enola.core.grpc.EnolaGrpcService

    GetThingsResponse getThings(GetThingsRequest r) throws EnolaException;

    // TODO Convert all callers to getThings, and remove this
    GetThingResponse getThing(GetThingRequest r) throws EnolaException;
}
