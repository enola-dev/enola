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
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import dev.enola.gui.EnolaGui;
import dev.enola.core.proto.Entity;
import dev.enola.core.meta.proto.EntityRelationship;
import dev.enola.core.proto.ID;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.meta.proto.EntityKinds;


public class EntityListview {


    public ArrayList<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>();
    private EntityKind entityKind;
    private ID relatedEntity;
    private EnolaListviewParameters enolaListviewParameters;
    private ArrayList<ArrayList<CellData>> data;
    private ID scope;

    // defaults to many if no cardinality defined
    public boolean isMany(EntityRelationship rel){
        if (rel == null || !rel.getTagsMap().containsKey("cardinality")) {
            return true;
        }
        return rel.getTagsMap().get("cardinality").equals("many");
    }

    public EntityListview(ID scope, ArrayList<Entity> entities_, EntityKind ek, ID relatedEntity, String relationship, String query) throws Exception {
        this.entityKind = ek;
        this.scope = scope;
        this.relatedEntity = relatedEntity;

        for (var id : ek.getId().getPathsList()) {
            columnDescriptors.add(new ColumnDescriptor("uid." + id, id, AggregationMethod.COUNT));
        }

        // related
        for (var related : ek.getRelatedMap().entrySet()) {
            columnDescriptors.add(new ColumnDescriptor("related." + related.getKey(), related.getValue().getLabel(), AggregationMethod.COUNT, false, false));
        }

        // data
        for (var data : ek.getDataMap().entrySet()) {
            // todo get the right aggregation method...
            var isNumber = (data.getValue().getTagsMap().containsKey("type") && data.getValue().getTagsMap().get("type").equals("integer"));
            var am = AggregationMethod.COUNT;
            if (data.getValue().getTagsMap().containsKey("aggregate") && data.getValue().getTagsMap().get("aggregate").equals("sum")){
                am = AggregationMethod.SUM;
            }
            columnDescriptors.add(new ColumnDescriptor("data." + data.getKey(), data.getValue().getLabel(), am, false, isNumber));
        }

        // links
        for (var link : ek.getLinkMap().entrySet()) {
            columnDescriptors.add(new ColumnDescriptor("link." + link.getKey(), link.getValue().getLabel(), AggregationMethod.COUNT, true, false));
        }

        if (query == null || query.isEmpty()) {
            this.enolaListviewParameters = new EnolaListviewParameters(columnDescriptors);
        } else {
            this.enolaListviewParameters = new EnolaListviewParameters(query, columnDescriptors);
        }

        var entities = new ArrayList<Entity>();

        for (var e : entities_) {
            var add = true;
            for (var filter : this.enolaListviewParameters.filteron.entrySet()){
                var key = filter.getKey();
                var pattern = filter.getValue();
                if (key.startsWith("data.")) {
                    var field = key.replace("data.", "");
                    if (!e.getDataMap().get(field).contains(pattern)) {
                      add = false;
                    }
                }
                if (key.startsWith("related.")) {
                    var field = key.replace("related.", "");
                    //var related = e.getDataMap().get(field);
                    var value = EnolaGui.getPath(e.getRelatedMap().get(field));
                    if (!value.contains(pattern)) {
                      add = false;
                    }
                }
            }
            if (add) {
                entities.add(e);
            }
        }

        // translate entities to data
        data = new ArrayList<ArrayList<CellData>>();
        for (var entity : entities) {
            var row = new ArrayList<CellData>();
            for (var i=0; i< ek.getId().getPathsList().size(); i++) {
                row.add(new CellData(entity.getId().getPathsList().get(i), EnolaGui.linkToEntityView(entity.getId())));
            }

            for (var related : ek.getRelatedMap().entrySet()) {
                ID relatedId = entity.getRelatedMap().get(related.getKey());
                var uri = "";
                var filterlink = "";
                var filterLabel = "";
                String label;
                if (isMany(related.getValue())) {
                    uri = EnolaGui.linkToEntitiesView(relatedId, entity.getId(), related.getKey());
                    label = related.getValue().getLabel();
                } else {
                    uri = EnolaGui.linkToEntityView(relatedId);
                    label = EnolaGui.getPath(relatedId);

                    if (enolaListviewParameters.hasFilter("related." + related.getKey(), label)) {
                        filterlink = enolaListviewParameters.getQueryWithoutFilter("related." + related.getKey(), label);
                        filterLabel = "&#607;";//"u"; ɟ
                    } else {
                        filterlink = enolaListviewParameters.getQueryWithFilter("related." + related.getKey(), label);
                        filterLabel = "f";
                    }

                }
                var cell = new CellData(label, uri, null, filterLabel,"?"+filterlink, true);
                cell.Dump();
                row.add(cell);
            }

            // data
            for (var data : ek.getDataMap().entrySet()) {
                var entry = entity.getDataMap().get(data.getKey());
                var filterlink = "";
                var filterLabel = "";
                if (enolaListviewParameters.hasFilter("data." + data.getKey(), entry)) {
                    filterlink = enolaListviewParameters.getQueryWithoutFilter("data." + data.getKey(), entry);
                    filterLabel = "&#607;";//"u";
                } else {
                    filterlink = enolaListviewParameters.getQueryWithFilter("data." + data.getKey(), entry);
                    filterLabel = "f";
                }
                row.add(new CellData(entry, null, null, filterLabel,"?"+filterlink, true));
            }

            // links
            for (var link : ek.getLinkMap().entrySet()) {
                row.add(new CellData("asdf","sfgh"));
            }
            data.add(row);
        }

    }


    public String getHtml(){
        var title  = new CellData(EnolaGui.getPath(scope));
        var views = new HashMap<String, ArrayList<String>> ();
        for (var view: entityKind.getViewsList()) {
            var columns = new ArrayList<String>();
            for (var n : view.getColumnsList()) {
                columns.add(n.getName());
            }
            views.put(view.getName(), columns);
        }
        views.put("all", enolaListviewParameters.getAllColumns());
        EnolaListview enolaListview = new EnolaListview(title, views, data, columnDescriptors, enolaListviewParameters);
        return enolaListview.getHtml();
    }
}
