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
package dev.enola.common.protobuf;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TypeRegistry;

public class TypeRegistryDescriptorProvider implements DescriptorProvider {

    private final TypeRegistry typeRegistry;

    public TypeRegistryDescriptorProvider(TypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }

    @Override
    public Descriptor getDescriptorForTypeUrl(String messageTypeURL) {
        try {
            return typeRegistry.getDescriptorForTypeUrl(messageTypeURL);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(messageTypeURL, e);
        }
    }

    @Override
    public Descriptor findByName(String name) {
        return typeRegistry.find(name);
    }
}
