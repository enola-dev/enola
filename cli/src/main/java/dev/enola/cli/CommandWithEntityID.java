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

import com.google.protobuf.TypeRegistry;

import dev.enola.common.io.resource.WriterResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.ID;

import picocli.CommandLine;

import java.io.IOException;

public abstract class CommandWithEntityID extends CommandWithModel {

    @CommandLine.Option(
            names = {"--format", "-f"},
            required = true,
            defaultValue = "YAML",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Output Format")
    Format format;

    // TODO @Parameters(index = "0..*") List<String> ids;
    // TODO ID instead of String, with https://picocli.info/#_strongly_typed_everything
    @CommandLine.Parameters(index = "0", paramLabel = "id", description = "ID of Entity")
    String idString;

    private WriterResource resource;
    private TypeRegistry typeRegistry;

    @Override
    protected final void run(EntityKindRepository ekr) throws Exception {
        // TODO Move elsewhere for continuous ("shell") mode, as this is "expensive".
        var esp = new EnolaServiceProvider();
        EnolaService service = esp.get(ekr);
        typeRegistry = esp.getTypeRegistry();

        ID id = IDs.parse(idString); // TODO replace with ITypeConverter
        // TODO Validate id; here it must have ns+name+path!

        var ek = ekr.get(id);
        resource = new WriterResource(out, format.toMediaType());

        run(ekr, service, ek, id);
    }

    protected abstract void run(
            EntityKindRepository ekr, EnolaService service, EntityKind ek, ID id) throws Exception;

    protected void write(EntityKind ek, Entity entity) throws IOException {
        new ProtoIO(typeRegistry).write(entity, resource);
    }
}
