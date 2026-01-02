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
package dev.enola.core.message;

import dev.enola.common.protobuf.DescriptorProvider;
import dev.enola.core.thing.ThingConnector;
import dev.enola.thing.message.MessageToThingConverter;

public abstract class ProtoToThingConnector implements ThingConnector {
    // TODO Move to package dev.enola.thing.message ?

    protected final MessageToThingConverter m2t = new MessageToThingConverter();
    protected final DescriptorProvider descriptorProvider;

    protected ProtoToThingConnector(DescriptorProvider descriptorProvider) {
        this.descriptorProvider = descriptorProvider;
    }
}
