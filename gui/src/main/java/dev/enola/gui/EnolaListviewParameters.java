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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static java.util.stream.Collectors.*;

// Interpunctus: yes this is prototyping code, it is ugly, unsafe, untested by the sheer definition of prototype.
// This should ensures it will never be used in production ;-)
// on top of that I do not really enjoy coding in Java... so it is poor Java as well
public class EnolaListviewParameters {
    private ArrayList<String> columns = new ArrayList<String>();
    public ArrayList<String> orderby = new ArrayList<String>();
    public ArrayList<String> groupby = new ArrayList<String>();
    public HashMap<String,String> filteron = new HashMap<String,String>();

    public ArrayList<String> getColumns(){
        var res = new ArrayList<String>();
        for (var c : columns){
            if (c.charAt(0) == '+') {
                res.add(c.substring(1));
            }
        }
        return res;
    }

    public ArrayList<String> getAllColumns(){
        var res = new ArrayList<String>();
        for (var c : columns){
             res.add(c.substring(1));
        }
        return res;
    }

    public EnolaListviewParameters(ArrayList<ColumnDescriptor> columnDescriptors){
        for (var c : columnDescriptors) {
            columns.add("+" + c.internalName);
            orderby.add("+" + c.internalName);
        }
    }

    public EnolaListviewParameters(ArrayList<ColumnDescriptor> columnDescriptors, ArrayList<String> visibleColumns){
    for (var c : columnDescriptors) {
        if (visibleColumns.contains(c.internalName)) {
            columns.add("+" + c.internalName);
        } else {
            columns.add("-" + c.internalName);
        }
        orderby.add("+" + c.internalName);
    }
}

    public EnolaListviewParameters(String query, ArrayList<ColumnDescriptor> columnDescriptors){
        for (String part : query.split("&")) {
            if  (part.split("=").length < 2) {
                continue;
            }
            if (part.split("=")[0].equals("orderby")){
                for (String column : part.split("=")[1].split(",")) {
                    orderby.add(column);
                }
            }
            if (part.split("=")[0].equals("groupby")) {
                for (String column : part.split("=")[1].split(",")) {
                    groupby.add(column);
                }
            }
            if (part.split("=")[0].equals("columns")) {
                for (String column : part.split("=")[1].split(",")) {
                    columns.add(column);
                }
            }
            if (part.split("=")[0].equals("filteron")) {
                for (String filter : part.split("=")[1].split(",")) {
                    filteron.put(filter.split(":")[0], filter.split(":")[1]);
                }
            }
        }
    }


    private String columnSection(){
        return "columns=" + String.join(",", columns);
    }

    private String groupbySection(){
        return "groupby=" + String.join(",", groupby);
    }

    private String orderbySection(){
        return "orderby=" + String.join(",", orderby);
    }

    private String filteronSection(HashMap<String,String> f){
        var section = "filteron=";
        var comma = "";
        for (var filter: f.entrySet()) {
          section += comma + filter.getKey() + ":" + filter.getValue();
          comma = ",";
        }
        return section;
    }

    public boolean getOrderbyDesc(String columnName){
        return orderby.contains("+"+columnName);
    }


    public String getWithColumns(ArrayList<String> columnNames){
        var updatedColumns = new ArrayList<String>();
        for (var col : columnNames) {
            updatedColumns.add("+" + col);
        }
        for (var col : columns) {
            if (!columnNames.contains(col.substring(1))) {
                updatedColumns.add("-" + col.substring(1));
            }
        }
        return
            "columns=" + String.join(",", updatedColumns) +
            "&" + orderbySection() +
            "&" + groupbySection() +
            "&" + filteronSection(filteron);
    }

    // get the section to get to change the sorting direction
    public String getQueryToggledOrderbyDirection(String columnName){
        var  updatedOrderby = new ArrayList<String>((ArrayList)orderby.clone());
        String updatedValue = "-" + columnName;
        var index = orderby.indexOf("+" + columnName);
        if (index == -1) {
            index = orderby.indexOf("-" + columnName);
            updatedValue = "+" + columnName;
        }
        updatedOrderby.set(index, updatedValue);
        return
            columnSection() +
            "&" + "orderby=" + String.join(",", updatedOrderby) +
            "&" + groupbySection() +
            "&" + filteronSection(filteron);
    }

    // get the section to get to move the column in the first position
    public String getQueryToggledOrderbyOrder(String columnName){
        var updatedOrderby = new ArrayList<String>((ArrayList)orderby.clone());
        var index = orderby.indexOf("+" + columnName);
        if (index == -1) {
            index = orderby.indexOf("-" + columnName);
        }
        updatedOrderby.remove(index);
        updatedOrderby.add(0, orderby.get(index));
        //updatedOrderby.set(index, updatedValue);
        return
            columnSection() +
            "&" + "orderby=" + String.join(",", updatedOrderby) +
            "&" + groupbySection() +
            "&" + filteronSection(filteron);
    }

    public int getOrderbyColumnOrderIndex(String columnName){
        var index = orderby.indexOf("+" + columnName);
        if (index == -1) {
            index = orderby.indexOf("-" + columnName);
        }
        return index;
    }


    public boolean getGroupbyGrouped(String columnName){
        return groupby.contains(columnName);
    }

    public String getQueryToggledGroupby(String columnName){
        var updatedGroupby = new ArrayList<String>((ArrayList)groupby.clone());
        var index = groupby.indexOf(columnName);
        if (index == -1){
            updatedGroupby.add(columnName);
        } else {
            updatedGroupby.remove(index);
        }
        return
            columnSection() +
            "&" + orderbySection() +
            "&" + "groupby=" + String.join(",", updatedGroupby) +
            "&" + filteronSection(filteron);
    }

    public int getGroupbyColumnOrderIndex(String columnName){
        return groupby.indexOf(columnName);
    }

    public String getQueryToggledGroupbyOrder(String columnName){
        var updatedGroupby = new ArrayList<String>((ArrayList)groupby.clone());
        var index = groupby.indexOf(columnName);
        updatedGroupby.remove(index);
        updatedGroupby.add(0, columnName);
        return
            columnSection() +
            "&" + orderbySection() +
            "&" + "groupby=" + String.join(",", updatedGroupby) +
            "&" + filteronSection(filteron);
    }

    public String getQueryToggledMoveLeft(String columnName){
        var updatedColumns = new ArrayList<String>((ArrayList)columns.clone());
        var index = columns.indexOf(columnName);
        updatedColumns.remove(index);
        updatedColumns.add((index + columns.size() - 1) % (columns.size()), columnName);
        return
            "columns=" + String.join(",", updatedColumns) +
            "&" + orderbySection() +
            "&" + groupbySection() +
            "&" + filteronSection(filteron);
    }

     public String getQueryToggledMoveRight(String columnName){
        var updatedColumns = new ArrayList<String>((ArrayList)columns.clone());
        var index = columns.indexOf(columnName);
        updatedColumns.remove(index);
        updatedColumns.add((index+1) % columns.size(), columnName);
        return
            "columns=" + String.join(",", updatedColumns) +
            "&" + orderbySection() +
            "&" + groupbySection() +
            "&" + filteronSection(filteron);
    }

    public boolean hasFilter(String columnName, String pattern){
        for (var filter:filteron.entrySet()) {
            if (filter.getKey().equals(columnName) && filter.getValue().equals(pattern)) {
                return true;
            }
        }
        return false;
    }

    public String getQueryWithFilter(String columnName, String pattern){
        var updatedFilteron = new HashMap<String,String>();
        for (var filter:filteron.entrySet()) {
            if (filter.getKey().equals(columnName)) {

            } else {
                updatedFilteron.put(filter.getKey(), filter.getValue());
            }
        }
        updatedFilteron.put(columnName, pattern);
        return
            columnSection() +
            "&" + orderbySection() +
            "&" + groupbySection() +
            "&" + filteronSection(updatedFilteron);
    }

    public String getQueryWithoutFilter(String columnName, String pattern){
        var updatedFilteron = new HashMap<String,String>();
        for (var filter:filteron.entrySet()) {
            if (filter.getKey().equals(columnName)) {
                //
            } else {
                updatedFilteron.put(filter.getKey(), filter.getValue());
            }
        }
        return
            columnSection() +
            "&" + orderbySection() +
            "&" + groupbySection() +
            "&" + filteronSection(updatedFilteron);
    }


}


