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
package dev.enola.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.ArrayList;

import dev.enola.core.proto.ID;
import dev.enola.core.proto.Entity;
import dev.enola.core.meta.proto.EntityKind;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.core.meta.EntityKindRepository;


import dev.enola.core.connector.proto.AugmentRequest;
import dev.enola.core.connector.proto.ConnectorServiceGrpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class EnolaGui {

    public static EntityKindRepository ekr;
    public static void main(String[] args) throws Exception {
        //var g = new EnolaGui();
        // System.out.println(g.ParseLinkToIdView("/ui/entity/demo.book/a/b/c").toString());
        // System.out.println(g.ParseLinkToEntitiesView("/ui/entities/demo.book/on/borrowed/for/demo.book/a/b/c").toString());
        // var x = g.ParseLinkToEntitiesView("/ui/entities/demo.book/on/borrowed/for/demo.book/a/b/c");
        // var y = g.LinkToEntitiesView(x.t, x.u, x.v);
        // System.out.println(y);

        ekr = new EntityKindRepository().load(new ClasspathResource("switzerland-model2.textproto"));

        HttpServer server = HttpServer.create(new InetSocketAddress(8008), 0);
        server.createContext("/ui", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    // public String GetKind(Entity entity){
    //     return entity.getId().getNs() + "." + entity.getId().getEntity();
    // }

    public static String getFullPath(ID id){
        return getKind(id) + getPath(id);
    }

    public static String getPath(ID id){
        return String.join("/", id.getPathsList());
    }

    public static String getKind(ID  id){
        return id.getNs() + "." + id.getEntity();
    }

    public static String linkToEntityView(ID id) {
        return "/ui/entity/" +getKind(id) + "/" + getPath(id);
    }

    public static ID parseLinkToIdView(String uri)  {
        if (uri.charAt(0) == '/') uri = uri.substring(1);
        var parts = uri.split("/");
        var kindParts = parts[2].split("\\.");
        var id = ID.newBuilder().setNs(kindParts[0]).setEntity(kindParts[1]);
        for (int i=3; i<parts.length; i++) {
            id.addPaths(parts[i]);
        }
        return id.build();
    }

    public static String linkToEntitiesView(ID kind, ID scope, String relationship) {
        return "/ui/entities/" + getKind(kind) + "/on/" + relationship + "/for/" + getKind(scope) + "/" +  getPath(scope);
    }

    public static Triplet<ID, ID, String> parseLinkToEntitiesView(String uri)  {

        if (uri.charAt(0) == '/') uri = uri.substring(1);
        var parts = uri.split("/");
        var kindParts = parts[2].split("\\.");
        var kind = ID.newBuilder().setNs(kindParts[0]).setEntity(kindParts[1]);
        var relationship = parts[4];
        kindParts = parts[6].split("\\.");

        var scope = ID.newBuilder().setNs(kindParts[0]).setEntity(kindParts[1]);
        for (int i=7; i<parts.length; i++) {
            scope.addPaths(parts[i]);
        }
        return Triplet.with(kind.build(), scope.build(), relationship);
    }


    public static ArrayList<Pair<EntityKind,String>> ListToRelationships(ID kind){
        // no easy way of referencing a related entry
        ArrayList<Pair<EntityKind,String>> res = new ArrayList<Pair<EntityKind,String>> ();
        System.out.println("kind.entity:" + kind.getEntity());
        for (var ek : ekr.list()){
            System.out.println("    ek.entity:" + ek.getId().getEntity());
            for (var related: ek.getRelatedMap().entrySet()) {
                System.out.println("       compare:" + getKind(related.getValue().getId()) + " " + getKind(kind));
                if (getKind(related.getValue().getId()).equals(getKind(kind))) {
                    // ek has a relationship to kind
                    System.out.println("           adding:" +  getKind(ek.getId()) + "  " + related.getKey());
                    res.add(Pair.with(ek,related.getKey()));
                }
            }
        }
        return res;
    }



    // http://localhost:8000/ui/entities/demo.canton/for/cantons/on/demo.country/switzerland

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "empty ;-)";
            try {
                String query = t.getRequestURI().getQuery();
                System.out.println("query:" + query);

                String p = t.getRequestURI().toString();
                System.out.println(p);
                p = p.split("\\?")[0];

                ID relatedEntity = ID.newBuilder().build();
                String relationship = "";
                ID scope = ID.newBuilder().build();
                ID kind = ID.newBuilder().build();;

                if (p.startsWith("/ui/entities")) {
                    //var t = g.ParseLinkToEntitiesView("/ui/entities/demo.book/on/borrowed/for/demo.book/a/b/c");
                    var triplet = parseLinkToEntitiesView(p);
                    kind = triplet.t;
                    scope = triplet.u;
                    relationship = triplet.v;
                }
                if (p.startsWith("/ui/entity")) {
                    kind = parseLinkToIdView(p);
                    scope = parseLinkToIdView(p);
                    relationship = "";
                }

                System.out.println("kind:" + kind.toString());
                System.out.println("scope:" + scope.toString());
                System.out.println("rel:" + relationship);

                var ek = ekr.getOptional(kind).get();

                ArrayList<Entity> entities = new ArrayList<Entity>();

                var endpoint = "localhost:8080";
                var credz = InsecureChannelCredentials.create();
                ManagedChannel channel = Grpc.newChannelBuilder(endpoint, credz).build();
                var client = ConnectorServiceGrpc.newBlockingStub(channel);

                var request = AugmentRequest.newBuilder();
                //request.setScope(ID.newBuilder().setNs("demo").setEntity("country").addPaths("switzerland").build());
                //request.setKind(ID.newBuilder().setNs("demo").setEntity("canton").build());
                request.setScope(scope);
                request.setKind(kind);
                request.setRelationship(relationship);
                System.out.println("calling connector...");
                var augmentResponse = client.augment(request.build());
                System.out.println("called  connector...");
                for (var e : augmentResponse.getEntitiesList()){
                    entities.add(e);
                }



                // get hold of the query string and

                EntityListview entityListview = new EntityListview(scope, entities, ek, relatedEntity, "", query);

                response = entityListview.getHtml();
                System.out.println("--------------------");
                System.out.println(response.length());
                System.out.println("--------------------");
            } catch (Exception e) {
                e.printStackTrace();
                response = e.toString();
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }}


