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
package dev.enola.demo;

import dev.enola.core.proto.Entity;
import dev.enola.core.proto.ID;
import dev.enola.core.connector.proto.AugmentRequest;
import dev.enola.core.connector.proto.AugmentResponse;
import dev.enola.core.connector.proto.ConnectorServiceGrpc;

import io.grpc.stub.StreamObserver;

import java.util.*;

public class DemoConnector extends ConnectorServiceGrpc.ConnectorServiceImplBase {
    @Override
    public void augment(AugmentRequest request, StreamObserver<AugmentResponse> responseObserver) {
        var entities = getEntities(request.getKind(), request.getScope(), request.getRelationship());
        var response = AugmentResponse.newBuilder();
        for (var e : entities){
            response.addEntities(e);
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    public ID makeId(String name, String path){
        return ID.newBuilder().setNs("demo").setEntity(name).addPaths(path).build();
    }

    public ID makeKind(String name){
        return ID.newBuilder().setNs("demo").setEntity(name).build();
    }

    public ArrayList<Entity> getEntities(ID kind, ID scope, String relationship){
        System.out.println("getPath(kind):" + getPath(kind));
        System.out.println("getPath(scope):" + getPath(scope));
        System.out.println("relationship:" + relationship);

        Entity switzerland = Entity.newBuilder()
                                .setId(makeId("country","switzerland"))
                                .putRelated("municipalities", makeKind("municipality"))
                                .putRelated("cantons", makeKind("canton"))
                                .build();

        var fribourg = Entity.newBuilder().setId(makeId("canton","fribourg"))
                        .putRelated("municipalities", makeKind("municipality"))
                        .putRelated("capital", makeId("municipality","fribourg"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("language", makeId("language","french"))
                        .putRelated("language", makeId("language","german"))
                        .putData("code", "FR")
                    .build();

        var vaud =     Entity.newBuilder().setId(makeId("canton","vaud"))
                        .putRelated("municipalities", makeKind("municipality"))
                        .putRelated("capital", makeId("municipality","lausanne"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("language", makeId("language","french"))
                        .putData("code", "VD")
                    .build();

        var zurich = Entity.newBuilder().setId(makeId("canton","zurich"))
                        .putRelated("municipalities", makeKind("municipality"))
                        .putRelated("capital", makeId("municipality","zurich"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("language", makeId("language","german"))
                        .putData("code", "ZH")
                    .build();

        var Zurich = Entity.newBuilder().setId(makeId("municipality","zurich"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("canton", makeId("canton", "zurich"))
                        .putRelated("language", makeId("language","german"))
                        .putData("postal_code", "8000")
                        .putData("inhabitants", "600000")
                        .putData("french", "false")
                        .putData("german", "true")
                        .putData("italian", "false")
                        .putData("rumantsch", "false")
                    .build();

        var Meilen = Entity.newBuilder().setId(makeId("municipality","meilen"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("canton", makeId("canton", "zurich"))
                        .putRelated("language", makeId("language","german"))
                        .putData("postal_code", "8706")
                        .putData("inhabitants", "8000")
                        .putData("french", "false")
                        .putData("german", "true")
                        .putData("italian", "false")
                        .putData("rumantsch", "false")
                    .build();

        var Fribourg = Entity.newBuilder().setId(makeId("municipality","fribourg"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("canton", makeId("canton", "fribourg"))
                        .putRelated("language", makeId("language","german"))
                        .putRelated("language", makeId("language","french"))
                        .putData("postal_code", "1700")
                        .putData("inhabitants", "60000")
                        .putData("french", "true")
                        .putData("german", "true")
                        .putData("italian", "false")
                        .putData("rumantsch", "false")
                    .build();
        var Gletterens = Entity.newBuilder().setId(makeId("municipality","gletterens"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("canton", makeId("canton", "fribourg"))
                        .putRelated("language", makeId("language","french"))
                        .putData("postal_code", "1544")
                        .putData("inhabitants", "728")
                        .putData("french", "true")
                        .putData("german", "false")
                        .putData("italian", "false")
                        .putData("rumantsch", "false")
                    .build();

        var Lausanne = Entity.newBuilder().setId(makeId("municipality","lausanne"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("canton", makeId("canton", "vaud"))
                        .putRelated("language", makeId("language","french"))
                        .putData("postal_code", "1000")
                        .putData("inhabitants", "150000")
                        .putData("french", "true")
                        .putData("german", "false")
                        .putData("italian", "false")
                        .putData("rumantsch", "false")
                    .build();

        var Sottens = Entity.newBuilder().setId(makeId("municipality","sottens"))
                        .putRelated("country", makeId("country","switzerland"))
                        .putRelated("canton", makeId("canton", "vaud"))
                        .putRelated("language", makeId("language","french"))
                        .putData("postal_code", "1062")
                        .putData("inhabitants", "230")
                        .putData("french", "true")
                        .putData("german", "false")
                        .putData("italian", "false")
                        .putData("rumantsch", "false")
                    .build();

        var french   = Entity.newBuilder().setId(makeId("language","french")).build();
        var german   = Entity.newBuilder().setId(makeId("language","german")).build();
        var italian  = Entity.newBuilder().setId(makeId("language","italian")).build();
        var rumantsch = Entity.newBuilder().setId(makeId("language","rumantsch")).build();


        var countries = new Entity[] {switzerland};
        var languages = new Entity[] {french, german, italian, rumantsch};
        var cantons = new Entity[] {fribourg, vaud, zurich};
        var municipalities = new Entity[] {Fribourg, Lausanne, Zurich, Sottens, Gletterens, Meilen};

        var entitiesByKind = new HashMap<String,Entity[]>();
        entitiesByKind.put("country", countries);
        entitiesByKind.put("language", languages);
        entitiesByKind.put("canton", cantons);
        entitiesByKind.put("municipality", municipalities);

        var entitiesByRelationship = new HashMap<String,Entity[]>();
        // dumb encoding of all relationships
        entitiesByRelationship.put("switzerland.cantons", cantons);
        entitiesByRelationship.put("switzerland.municipalities", municipalities);
        entitiesByRelationship.put("vaud.municipalities", new Entity[] {Lausanne, Sottens});
        entitiesByRelationship.put("zurich.municipalities", new Entity[] {Zurich, Meilen});
        entitiesByRelationship.put("fribourg.municipalities", new Entity[] {Fribourg, Gletterens});
        entitiesByRelationship.put("Vaud.capital", new Entity[] {Lausanne});
        entitiesByRelationship.put("Zurich.capital", new Entity[] {Zurich});
        entitiesByRelationship.put("Fribourg.capital", new Entity[] {Fribourg});
        entitiesByRelationship.put("fribourg.language", new Entity[] {french,german});
        entitiesByRelationship.put("gletterens.language", new Entity[] {french});
        entitiesByRelationship.put("vaud.language", new Entity[] {french});
        entitiesByRelationship.put("sottens.language", new Entity[] {french});
        entitiesByRelationship.put("zurich.language", new Entity[] {german});
        entitiesByRelationship.put("meilen.language", new Entity[] {german});

        var res = new ArrayList<Entity>();


        if (relationship != null && !relationship.isEmpty()) {
            var key = scope.getPaths(0) + "." + relationship;
            if (entitiesByRelationship.containsKey(key)){
                res.addAll(Arrays.asList(entitiesByRelationship.get(key)));

            }
        }
        // filter by scope on  kind
        for (var e : entitiesByKind.get(kind.getEntity())) {
            if (relationship == null || relationship.isEmpty()) {
                if (getPath(scope).equals(getPath(e.getId()))) {
                    res.add(e);
                    break;
                }
             }
        }
        System.out.println("returning " + res.size() + " entities");
        System.out.println("");
        return res;
    }

    public String getPath(ID id){
        if (id.getPathsCount() == 0) {
            return id.getNs() + "." + id.getEntity();
        } else {
            return id.getNs() + "." + id.getEntity() + "/" + String.join("/", id.getPathsList());

        }
    }
}
