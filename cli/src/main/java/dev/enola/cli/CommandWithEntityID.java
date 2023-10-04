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
package dev.enola.cli;

import com.google.protobuf.Empty;

import dev.enola.common.io.resource.WriterResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.TypeRegistryWrapper;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;

import picocli.CommandLine;

import java.io.IOException;

public abstract class CommandWithEntityID extends CommandWithModel {

    @CommandLine.Option(
            names = {"--format", "-f"},
            required = true,
            defaultValue = "YAML",
            description = "Output Format: ${COMPLETION-CANDIDATES}; default=${DEFAULT-VALUE}")
    Format format;

    // TODO @Parameters(index = "0..*") List<String> ids;
    // TODO ID instead of String, with https://picocli.info/#_strongly_typed_everything
    @CommandLine.Parameters(index = "0", paramLabel = "id", description = "ID of Entity")
    String idString;

    private WriterResource resource;
    private TypeRegistryWrapper typeRegistryWrapper;

    @Override
    protected final void run(EntityKindRepository ekr, EnolaServiceBlockingStub service)
            throws Exception {
        var fds = service.getFileDescriptorSet(Empty.getDefaultInstance());
        typeRegistryWrapper = TypeRegistryWrapper.from(fds);

        ID id = IDs.parse(idString); // TODO replace with ITypeConverter
        // TODO Validate id; here it must have ns+name+path!

        var request1 = GetEntityRequest.newBuilder().setId(IDs.entityKind(id)).build();
        var response1 = service.getEntity(request1);
        var ek = response1.getEntity().getDataOrThrow("schema").unpack(EntityKind.class);

        resource = new WriterResource(out, format.toMediaType());

        run(service, ek, id);
    }

    protected abstract void run(EnolaServiceBlockingStub service, EntityKind ek, ID id)
            throws Exception;

    protected void write(Entity entity) throws IOException {
        new ProtoIO(typeRegistryWrapper.get()).write(entity, resource);
    }
}
