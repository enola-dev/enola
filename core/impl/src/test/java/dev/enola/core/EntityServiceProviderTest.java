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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import static dev.enola.core.meta.proto.FileSystemRepository.Format.FORMAT_YAML;

import static org.junit.Assert.assertThrows;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ReplacingResource;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.*;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.demo.Server;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

public class EntityServiceProviderTest {

    // TODO EntityKinds from file instead of programmatically constructing may be easier to read?

    @Test
    public void testTrivialEmptyEntityKind() throws EnolaException, ValidationException {
        var kid = ID.newBuilder().setNs("test").setEntity("empty").addPaths("name").build();
        var kind = EntityKind.newBuilder().setId(kid).build();
        var ekr = new EntityKindRepository().put(kind);
        var service = new EnolaServiceProvider().get(ekr);

        var eid = ID.newBuilder(kid).clearPaths().addPaths("whatever").build();
        var eri = IDs.toPath(eid);
        var request = GetEntityRequest.newBuilder().setEri(eri).build();
        var response = service.getEntity(request);
        var entity = response.getEntity();
        assertThat(entity.getId()).isEqualTo(eid);
        assertThat(entity.getTs().getSeconds()).isGreaterThan(123);
    }

    @Test
    // TODO Separate tests for UriTemplate and FilestoreRepository
    public void testUriTemplateAndFilestoreRepository()
            throws ValidationException, EnolaException, IOException {
        var kid = ID.newBuilder().setNs("test").setEntity("dog").addPaths("name").build();
        var template = "https://www.google.com/search?q={path.name}+dog&sclient=img";
        var href = Link.newBuilder().setUriTemplate(template).build();
        var tid = ID.newBuilder().setNs("test").setEntity("dog").addPaths("{path.name}").build();
        var rel1 = EntityRelationship.newBuilder().setId(tid).build();
        var rel2 = EntityRelationship.newBuilder().setId(tid).build();
        var fs = FileSystemRepository.newBuilder().setPath(".").setFormat(FORMAT_YAML).build();
        var connector = Connector.newBuilder().setFs(fs).build();
        var kind =
                EntityKind.newBuilder()
                        .setId(kid)
                        .putLink("image", href)
                        .putRelated("rel1", rel1)
                        .putRelated("rel2", rel2)
                        .addConnectors(connector)
                        .build();
        var ekr = new EntityKindRepository().put(kind);
        var service = new EnolaServiceProvider().get(ekr);

        var path = Path.of("./test.dog/king-charles.yaml");
        path.getParent().toFile().mkdir();
        new FileResource(path).charSink().write("related:\n  rel1:\n    entity: \"cat\"");

        var eid = ID.newBuilder(kid).clearPaths().addPaths("king-charles").build();
        var eri = IDs.toPath(eid);
        var request = GetEntityRequest.newBuilder().setEri(eri).build();
        var response = service.getEntity(request);
        var entity = response.getEntity();

        assertThat(entity.getId()).isEqualTo(eid);
        assertThat(entity.getTs().getSeconds()).isGreaterThan(123);

        assertThat(entity.getLinkMap().get("image"))
                .isEqualTo("https://www.google.com/search?q=king-charles+dog&sclient=img");

        assertThat(entity.getRelatedOrThrow("rel1").getEntity()).isEqualTo("cat");

        assertThat(entity.getRelatedOrThrow("rel2").getNs()).isEqualTo("test");
        assertThat(entity.getRelatedOrThrow("rel2").getEntity()).isEqualTo("dog");
        assertThat(entity.getRelatedOrThrow("rel2").getPathsList()).containsExactly("king-charles");
    }

    @Test
    public void testErrorConnector() throws ValidationException, EnolaException {
        var kid = ID.newBuilder().setNs("test").setEntity("empty").addPaths("name").build();
        var ec = Connector.newBuilder().setError("failed!").build();
        var kind = EntityKind.newBuilder().setId(kid).addConnectors(ec).build();
        var ekr = new EntityKindRepository().put(kind);
        var service = new EnolaServiceProvider().get(ekr);

        var eid = ID.newBuilder(kid).clearPaths().addPaths("whatever").build();
        var eri = IDs.toPath(eid);
        var request = GetEntityRequest.newBuilder().setEri(eri).build();
        var ex = assertThrows(EnolaException.class, () -> service.getEntity(request));
        assertThat(ex.getMessage()).isEqualTo("failed!");
    }

    @Test
    public void testEntityValidation() throws ValidationException, EnolaException {
        var kid = ID.newBuilder().setNs("test").setEntity("empty").addPaths("name").build();
        var ivc =
                Connector.newBuilder()
                        .setJavaClass(ValidationFailureAspect.class.getName())
                        .build();
        var kind = EntityKind.newBuilder().setId(kid).addConnectors(ivc).build();
        var ekr = new EntityKindRepository().put(kind);
        var service = new EnolaServiceProvider().get(ekr);

        var eid = ID.newBuilder(kid).clearPaths().addPaths("whatever").build();
        var eri = IDs.toPath(eid);
        var request = GetEntityRequest.newBuilder().setEri(eri).build();
        assertThrows(EnolaException.class, () -> service.getEntity(request));
    }

    @Test
    public void testGrpcConnector() throws IOException, ValidationException, EnolaException {
        try (var server = new Server().start(0)) {
            var port = Integer.toString(server.getPort());
            var model =
                    new ReplacingResource(
                            new ClasspathResource("demo-connector-model.textproto"), "9090", port);
            var ekr = new EntityKindRepository().load(model);
            var service = new EnolaServiceProvider().get(ekr);

            var eid = ID.newBuilder().setNs("demo").setEntity("foo").addPaths("whatever").build();
            var eri = IDs.toPath(eid);
            var request = GetEntityRequest.newBuilder().setEri(eri).build();
            var entity = service.getEntity(request).getEntity();
            assertThat(entity.getLinkOrThrow("link1")).isEqualTo("http://www.vorburger.ch");
        }
    }

    @Test
    public void testEntityKindInception() throws ValidationException, EnolaException {
        var kid = ID.newBuilder().setNs("enola").setEntity("entity_kind").addPaths("name").build();
        var sid = ID.newBuilder().setNs("enola").setEntity("schema").addPaths("fqn").build();

        var ekr = new EntityKindRepository();
        assertThat(ekr.listID()).containsExactly(kid, sid);
        var service = new EnolaServiceProvider().get(ekr);

        var eid = ID.newBuilder(kid).clearPaths().addPaths("enola.entity_kind").build();

        var eri = IDs.toPath(eid);
        var getRequest = GetEntityRequest.newBuilder().setEri(eri).build();
        var getResponse = service.getEntity(getRequest);
        assertWithMessage("data.schema")
                .that(getResponse.getEntity().getDataOrThrow("schema"))
                .isNotNull();

        var listRequest = ListEntitiesRequest.newBuilder().setEri(eri).build();
        var listResponse = service.listEntities(listRequest);
        assertThat(listResponse.getEntitiesList()).hasSize(2);
    }
}
