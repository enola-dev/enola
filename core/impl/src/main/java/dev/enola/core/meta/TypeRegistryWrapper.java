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
package dev.enola.core.meta;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.TypeRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO Optimization: This should allow clients like CLI to fetch as Map of Protos!
public class TypeRegistryWrapper {
    private final TypeRegistry originalTypeRegistry;
    private final FileDescriptorSet fileDescriptorSet;

    private TypeRegistryWrapper(TypeRegistry typeRegistry, FileDescriptorSet fileDescriptorSet) {
        this.originalTypeRegistry = typeRegistry;
        this.fileDescriptorSet = fileDescriptorSet;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static TypeRegistryWrapper from(FileDescriptorSet fileDescriptorSet)
            throws DescriptorValidationException {
        var builder = TypeRegistry.newBuilder();
        for (var fileDescriptorProto : fileDescriptorSet.getFileList()) {
            FileDescriptor[] dependencies = new FileDescriptor[0];
            FileDescriptor fileDescriptor =
                    FileDescriptor.buildFrom(fileDescriptorProto, dependencies, true);
            builder.add(fileDescriptor.getMessageTypes());
        }
        return new TypeRegistryWrapper(builder.build(), fileDescriptorSet);
    }

    public TypeRegistry get() {
        return originalTypeRegistry;
    }

    public FileDescriptorSet fileDescriptorSet() {
        return fileDescriptorSet;
    }

    public List<String> names() {
        var names = new ArrayList<String>();
        for (var file : fileDescriptorSet().getFileList()) {
            var pkg = file.hasPackage() ? file.getPackage() + "." : "";
            names.ensureCapacity(file.getMessageTypeCount());
            for (var message : file.getMessageTypeList()) {
                names.add(pkg + message.getName());
            }
        }
        return names;
    }

    public Descriptors.GenericDescriptor find(String name) {
        var descriptor = get().find(name);
        if (descriptor == null) {
            throw new IllegalArgumentException("Proto unknown: " + name);
        }
        return descriptor;
    }

    public static final class Builder {
        private final Set<String> files = new HashSet<>();
        private final TypeRegistry.Builder typeRegistryBuilder = TypeRegistry.newBuilder();
        private final FileDescriptorSet.Builder fileDescriptorBuilder =
                FileDescriptorSet.newBuilder();

        private Builder() {}

        public Builder add(Iterable<Descriptor> descriptors) {
            for (Descriptor type : descriptors) {
                typeRegistryBuilder.add(type);
                addFile(type.getFile());
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
        }

        public TypeRegistryWrapper build() {
            return new TypeRegistryWrapper(
                    typeRegistryBuilder.build(), fileDescriptorBuilder.build());
        }
    }
}
