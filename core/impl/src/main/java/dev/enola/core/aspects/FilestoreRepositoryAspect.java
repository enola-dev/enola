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
package dev.enola.core.aspects;

import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.EnolaException;
import dev.enola.core.EntityAspect;
import dev.enola.core.IDs;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;

import java.io.IOException;
import java.nio.file.Path;

public class FilestoreRepositoryAspect implements EntityAspect {

    private final Path root;
    private final Format format;
    private final ProtoIO io = new ProtoIO();

    public FilestoreRepositoryAspect(Path root, Format format) {
        this.root = root.toAbsolutePath().normalize();
        this.format = format;
    }

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException {
        var id = entity.getId();
        var path = root.resolve(IDs.toPath(id) + "." + format.extension);

        if (!path.toFile().exists()) {
            return;
        }

        ReadableResource resource = new FileResource(path);
        try {
            io.read(resource, entity);
        } catch (IOException e) {
            throw new EnolaException("Failed to read: " + resource.uri(), e);
        }
    }

    public enum Format {
        YAML("yaml"),
        JSON("json"),
        TEXTPB("textproto");

        private final String extension;

        Format(String extension) {
            this.extension = extension;
        }
    }
}
