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

import com.google.common.collect.ImmutableSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.TypeRegistry;

import java.util.List;

public class TypeRegistryWrapper {
    private final TypeRegistry originalTypeRegistry;
    private final ImmutableSet<String> names;

    private TypeRegistryWrapper(TypeRegistry typeRegistry, ImmutableSet<String> names) {
        this.originalTypeRegistry = typeRegistry;
        this.names = names;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public TypeRegistry get() {
        return originalTypeRegistry;
    }

    public ImmutableSet<String> names() {
        return names;
    }

    public static final class Builder {
        private final TypeRegistry.Builder originalBuilder = TypeRegistry.newBuilder();
        private final ImmutableSet.Builder<String> names = ImmutableSet.builder();

        private Builder() {}

        public Builder add(List<Descriptors.Descriptor> descriptors) {
            originalBuilder.add(descriptors);
            for (Descriptors.Descriptor type : descriptors) {
                addFile(type.getFile());
            }
            return this;
        }

        private void addFile(Descriptors.FileDescriptor file) {
            for (Descriptors.FileDescriptor dependency : file.getDependencies()) {
                addFile(dependency);
            }
            for (Descriptors.Descriptor message : file.getMessageTypes()) {
                addMessage(message);
            }
        }

        private void addMessage(Descriptors.Descriptor message) {
            for (Descriptors.Descriptor nestedType : message.getNestedTypes()) {
                addMessage(nestedType);
            }
            names.add(message.getFullName());
        }

        public TypeRegistryWrapper build() {
            return new TypeRegistryWrapper(originalBuilder.build(), names.build());
        }
    }
}
