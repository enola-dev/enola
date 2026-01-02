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
package dev.enola.common.protobuf;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.GenericDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.protobuf.TypeRegistry;

import java.util.HashSet;
import java.util.Set;

/**
 * TypeRegistryWrapper is a registry of ProtoBuf type descriptors.
 *
 * <p>While it's similar to ProtoBuf's own {@link TypeRegistry}, this one (a) has a {@link #names()}
 * method to enumerate all registered types' names, and (b) includes not just "message" but also
 * "enum" and "service".
 */
// TODO Rename to drop the *Wrapper suffix (it used to wrap TypeRegistry, but now does not anymore)
// TODO Optimization: This should allow clients like CLI to fetch as Map of Protos!
public class TypeRegistryWrapper implements DescriptorProvider {

    private final TypeRegistry originalTypeRegistry;
    private final ImmutableMap<String, GenericDescriptor> types;
    private final FileDescriptorSet fileDescriptorSet;

    private TypeRegistryWrapper(
            TypeRegistry typeRegistry,
            ImmutableMap<String, GenericDescriptor> types,
            FileDescriptorSet fileDescriptorSet) {
        this.types = types;
        this.originalTypeRegistry = typeRegistry;
        this.fileDescriptorSet = fileDescriptorSet;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static TypeRegistryWrapper from(FileDescriptorSet fileDescriptorSet)
            throws DescriptorValidationException {
        var builder = newBuilder();
        for (var fileDescriptorProto : fileDescriptorSet.getFileList()) {
            FileDescriptor[] noDependencies = new FileDescriptor[0];
            FileDescriptor fileDescriptor =
                    FileDescriptor.buildFrom(fileDescriptorProto, noDependencies, true);
            builder.add(fileDescriptor.getMessageTypes());
        }
        return builder.build();
    }

    public TypeRegistry get() {
        return originalTypeRegistry;
    }

    public FileDescriptorSet fileDescriptorSet() {
        return fileDescriptorSet;
    }

    public Set<String> names() {
        return types.keySet();
    }

    @Override
    public GenericDescriptor findByName(String name) {
        if (name == null) throw new IllegalArgumentException("name == null");
        if (name.isEmpty()) throw new IllegalArgumentException("name is empty");
        var descriptor = types.get(name);
        if (descriptor == null) {
            throw new IllegalArgumentException(
                    "Proto unknown: " + name + "; only knows: " + names());
        }
        return descriptor;
    }

    @Override
    public Descriptor getDescriptorForTypeUrl(String typeURL) {
        return (Descriptor) findByName(getTypeName(typeURL));
    }

    // This method is copy/pasted from com.google.protobuf.TypeRegistry
    private static String getTypeName(String typeUrl) throws IllegalArgumentException {
        String[] parts = typeUrl.split("/");
        if (parts.length == 1) {
            throw new IllegalArgumentException("Invalid type url found: " + typeUrl);
        }
        return parts[parts.length - 1];
    }

    // skipcq: JAVA-E0169
    public static final class Builder implements dev.enola.common.Builder<TypeRegistryWrapper> {
        private final Set<String> files = new HashSet<>();
        private ImmutableMap.Builder<String, GenericDescriptor> typesBuilder =
                ImmutableMap.builder();
        private final TypeRegistry.Builder typeRegistryBuilder = TypeRegistry.newBuilder();

        private FileDescriptorSet.Builder fileDescriptorBuilder = FileDescriptorSet.newBuilder();

        private Builder() {}

        public Builder add(Descriptor descriptor) {
            typeRegistryBuilder.add(descriptor);
            addFile(descriptor.getFile());
            return this;
        }

        public Builder add(Iterable<Descriptor> descriptors) {
            for (Descriptor descriptor : descriptors) {
                add(descriptor);
            }
            return this;
        }

        private void addFile(FileDescriptor file) {
            if (!files.add(file.getFullName())) {
                return;
            }
            for (FileDescriptor dependency : file.getDependencies()) {
                addFile(dependency);
            }
            fileDescriptorBuilder.addFile(file.toProto());

            for (Descriptor messageType : file.getMessageTypes()) {
                addDescriptor(messageType);
            }
            for (var enumType : file.getEnumTypes()) {
                addDescriptor(enumType);
            }
            for (var fieldType : file.getExtensions()) {
                addDescriptor(fieldType);
            }
            for (var serviceType : file.getServices()) {
                addDescriptor(serviceType);
            }
        }

        private void addDescriptor(Descriptor descriptor) {
            for (var nestedType : descriptor.getNestedTypes()) {
                addDescriptor(nestedType);
            }
            for (var nestedType : descriptor.getEnumTypes()) {
                addDescriptor(nestedType);
            }
            for (var nestedType : descriptor.getExtensions()) {
                addDescriptor(nestedType);
            }
            typesBuilder.put(descriptor.getFullName(), descriptor);
        }

        private void addDescriptor(EnumDescriptor descriptor) {
            typesBuilder.put(descriptor.getFullName(), descriptor);
        }

        private void addDescriptor(FieldDescriptor descriptor) {
            typesBuilder.put(descriptor.getFullName(), descriptor);
        }

        private void addDescriptor(ServiceDescriptor descriptor) {
            typesBuilder.put(descriptor.getFullName(), descriptor);
        }

        @Override
        public TypeRegistryWrapper build() {
            var types = typesBuilder.build();
            var typeRegistry = typeRegistryBuilder.build();
            var fileDescriptor = fileDescriptorBuilder.build();
            var wrapper = new TypeRegistryWrapper(typeRegistry, types, fileDescriptor);
            typesBuilder = null;
            fileDescriptorBuilder = null;
            return wrapper;
        }
    }
}
