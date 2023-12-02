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
package dev.enola.core.aspects;

import static dev.enola.common.protobuf.Timestamps2.fromInstant;

import com.google.protobuf.TypeRegistry;

import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.EnolaException;
import dev.enola.core.EntityAspect;
import dev.enola.core.IDs;
import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.meta.proto.FileSystemRepository;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// TODO Rename (refactor) to FilestoreRepositoryEntityAspect
// TODO Move (refactor) this into a package dev.enola.core.entity.aspect
public class FilestoreRepositoryAspect implements EntityAspect {

    private static final Logger LOG = LoggerFactory.getLogger(FilestoreRepositoryAspect.class);

    private final Path root;
    private final FileSystemRepository.Format format;
    private final ProtoIO io;

    public FilestoreRepositoryAspect(Path root, FileSystemRepository.Format format) {
        this.root = root.toAbsolutePath().normalize();
        this.format = format;

        var typeRegistryBuilder = TypeRegistry.newBuilder();
        // TODO https://github.com/enola-dev/enola/issues/238
        // typeRegistryBuilder.add(descriptor); where descriptor is read from
        // the proto files given in FileSystemRepository, loaded with square/wire.
        this.io = new ProtoIO(typeRegistryBuilder.build());
    }

    // TODO https://github.com/enola-dev/enola/issues/238 Implement @Override getDescriptors()
    // using the same as above (but that above is for reading files, whereas this is required
    // e.g. for the CLI to be able to print Any data fields.

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException {
        var id = entity.getId();
        var path = root.resolve(IDs.toPath(id) + "." + getExtension(format));

        if (!path.toFile().exists()) {
            LOG.info("No {}", path);
            return;
        }

        ReadableResource resource = new FileResource(path);
        try {
            io.read(resource, entity);
            resource.lastModifiedIfKnown().ifPresent(ts -> entity.setTs(fromInstant(ts)));
        } catch (IOException e) {
            throw new EnolaException("Failed to read: " + resource.uri(), e);
        }
    }

    @Override
    public void list(
            ConnectorServiceListRequest request,
            EntityKind entityKind,
            List<Entity.Builder> entities)
            throws EnolaException {

        var id = request.getId();
        var path = root.resolve(IDs.toPath(id));

        if (!path.toFile().exists()) {
            LOG.info("No {}", path);
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entityPath : stream) {
                var newID =
                        ID.newBuilder()
                                .setNs(entityKind.getId().getNs())
                                .setEntity(entityKind.getId().getEntity())
                                // TODO .addAllPaths(...) !!
                                .build();
                var newEntity = Entity.newBuilder().setId(newID);
                augment(newEntity, entityKind);
            }
        } catch (IOException e) {
            throw new EnolaException("Failed to read " + path, e);
        }
    }

    private String getExtension(FileSystemRepository.Format format) {
        switch (format) {
            case FORMAT_TEXTPROTO:
                return "textproto";
            case FORMAT_YAML:
                return "yaml";
            case FORMAT_JSON:
                return "json";
        }
        throw new IllegalArgumentException("Unknown format: " + format);
    }
}
