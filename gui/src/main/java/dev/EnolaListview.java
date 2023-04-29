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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import static java.util.stream.Collectors.*;

public class EnolaListview {
    public ArrayList<ArrayList<CellData>> data;
    public ArrayList<ColumnDescriptor> columnDescriptors;
    public EnolaListviewParameters enolaListviewParameters;
    public HashMap<String,Integer> nameToIndex = new HashMap<String,Integer>();
    public EnolaListviewGroup enolaListviewGroup;

    // index of array is actual displayed column, value is index of column in data
    public ArrayList<Integer> columnMappings = new ArrayList<Integer>(Arrays.asList(0,1,2,3));

    private int compareTo(int index, ArrayList<CellData> a, ArrayList<CellData> b, ArrayList<String> orderby_) {
        final ArrayList<String> orderby = new ArrayList<String>((ArrayList)orderby_.clone());
        if (index >= orderby.size()) {
            return 0;
        }
        String colName = orderby.get(index).substring(1);
        int colIndex = nameToIndex.get(colName);
        if (a.get(colIndex).equals(b.get(colIndex))) {
            return compareTo(index+1, a, b, orderby);
        }
        if (orderby.get(index).contains("+")){
            return a.get(colIndex).compareTo(b.get(colIndex));
        } else  {
            return b.get(colIndex).compareTo(a.get(colIndex));
        }
    }

    public EnolaListview(ArrayList<ArrayList<CellData>> data, ArrayList<ColumnDescriptor> columnDescriptors, EnolaListviewParameters enolaListviewParameters){
        this.data = data;
        this.columnDescriptors = columnDescriptors;
        this.enolaListviewParameters = enolaListviewParameters;
        for (int i=0; i<columnDescriptors.size(); i++) {
            nameToIndex.put(columnDescriptors.get(i).name, i);
        }

        // order colums to match URI requirements
        Collections.sort(data, (thisRow,thatRow) -> compareTo(0, thisRow, thatRow, enolaListviewParameters.orderby));

        var allColumnIndices = new HashSet<Integer>();
        for (int i=0; i<columnDescriptors.size(); i++) {
            allColumnIndices.add(i);
        }
        var orderbyIndices = new ArrayList<Integer>();
        for (var v: enolaListviewParameters.orderby){
            orderbyIndices.add(nameToIndex.get(v.substring(1)));
        }
        var groupbyIndices = new ArrayList<Integer>();
        for (String gp : enolaListviewParameters.groupby){
            groupbyIndices.add(nameToIndex.get(gp));
        }
        // populate columnMappings
        columnMappings.clear();
        for (String columnName : enolaListviewParameters.columns) {
            columnMappings.add(nameToIndex.get(columnName));
        }

        enolaListviewGroup = new EnolaListviewGroup(this, enolaListviewParameters, data, allColumnIndices, groupbyIndices);
    }

    private String emitRow(ArrayList<Triplet<Integer,Integer, String>> rows){
        var rowByIndex = new HashMap<Integer,Triplet<Integer,Integer, String>>();
        for (var row : rows) {
            rowByIndex.put(row.t, row);
        }
        String r= "<tr>";
        for (int i=0; i<columnMappings.size(); i++) {
            // not all three values are always  populated, hence the column index
            if (!rowByIndex.containsKey(columnMappings.get(i))) {
                continue;
            }
            var row = rowByIndex.get(columnMappings.get(i));
            r += "<td rowspan=\"" + row.u + "\">";
            r += row.v;
            r += "</td>";
        }
        r += "</tr>";
        return r;
    }

    public String getHtml(){

        String r = "<head>\n";
        r += "<style>table, th, td {  border: 1px solid black;   border-collapse: collapse;}</style>\n";
        r += "</head>\n";
        r += "<table>\n";
        r += "<tr>";
        for (int j=0; j<columnDescriptors.size(); j++) {
            if (!enolaListviewParameters.columns.contains(columnDescriptors.get(j).name)) {
                continue;
            }
            var i = columnMappings.get(j);
            String name = columnDescriptors.get(i).name;
            r += "<th>";
            r += name;

            // COLUMNS LINKS
            // ORDER BY LINKS
            r += "<br>\n";
            String query = enolaListviewParameters.getQueryToggledMoveLeft(name);
            if (query.startsWith("&")) query = query.substring(1);
            r += "<a href=http://localhost:8000/test1?" + query + ">" + "   &lArr;   " + "<a/>";

            String orderDirectionLabel =
            query = enolaListviewParameters.getQueryToggledOrderbyDirection(name);
            if (query.startsWith("&")) query = query.substring(1);
            r += "<a href=http://localhost:8000/test1?" + query + ">"
                + (enolaListviewParameters.getOrderbyDesc(name)?"&uArr;":"&dArr;")
                + "<a/>";

            //sorting order between columns
            int orderColumnLabel = enolaListviewParameters.getOrderbyColumnOrderIndex(name);
            query = enolaListviewParameters.getQueryToggledOrderbyOrder(name);
            if (query.startsWith("&")) query = query.substring(1);
            r += "<a href=http://localhost:8000/test1?" + query + ">  " + orderColumnLabel +"  <a/>";

                        query = enolaListviewParameters.getQueryToggledMoveRight(name);
            if (query.startsWith("&")) query = query.substring(1);
            r += "<a href=http://localhost:8000/test1?" + query + ">" + "&rArr;" + "<a/>";


            // GROUP BY LINKS
            r += "<br>\n";
            var groupedLabel = enolaListviewParameters.getGroupbyGrouped(name)?"UNGROUP":"GROUP";
            query = enolaListviewParameters.getQueryToggledGroupby(name);
            if (query.startsWith("&")) query = query.substring(1);
            r += "<a href=http://localhost:8000/test1?" + query + ">" + groupedLabel +"<a/>";

            var groupOrderLabel = enolaListviewParameters.getGroupbyColumnOrderIndex(name);
            if (groupOrderLabel != -1) {
                query = enolaListviewParameters.getQueryToggledGroupbyOrder(name);
                if (query.startsWith("&")) query = query.substring(1);
                r += "<a href=http://localhost:8000/test1?" + query + ">    " + groupOrderLabel +"<a/>";
            }

            r += "</th>\n";
        }
        r += "</tr>\n";
        var row = new ArrayList<Triplet<Integer, Integer, String>>();
        r += generateGroupByHtml(enolaListviewGroup, row);
        r += "</table>\n";
        return r;
    }

    private String generateGroupByHtml(EnolaListviewGroup enolaListviewGroup, ArrayList<Triplet<Integer, Integer, String>> row){
        var r = "";
        // leaf columns
        if (enolaListviewGroup.group.size() == 0) {
            //sort before displaying...
            final ArrayList<String> orderby = new ArrayList<String>();
            for (var col : this.enolaListviewParameters.orderby){
                if (enolaListviewGroup.remainingColumns.contains(nameToIndex.get(col.substring(1)))) {
                    orderby.add(col);
                }
            }
            Collections.sort(enolaListviewGroup.data, (thisRow,thatRow) -> compareTo(0, thisRow, thatRow, orderby));

            if (enolaListviewGroup.remainingColumns.size() != columnDescriptors.size()) {
                // grouping
                // accumulate/collect
                for (int i : enolaListviewGroup.remainingColumns){
                    if (columnDescriptors.get(i).aggregationMethod == AggregationMethod.SUM) {
                        Long acc = 0L;
                        for (int j=0; j < enolaListviewGroup.data.size(); j++) {
                            acc += Long.parseLong(enolaListviewGroup.data.get(j).get(i).value);
                        }
                        row.add(Triplet.with(i, 1, Long.toString(acc)));
                    } else {
                        String acc = ""; var comma = "";
                        for (int j=0; j < enolaListviewGroup.data.size(); j++) {
                            acc += comma + enolaListviewGroup.data.get(j).get(i);
                            comma = ",";
                        }
                        row.add(Triplet.with(i, 1, acc));
                    }
                }
                r += emitRow(row);
                row.clear();
                return r;
            } else {
                // no grouping
                for (int j=0; j < enolaListviewGroup.data.size(); j++) {
                    for (int i : enolaListviewGroup.remainingColumns){
                        CellData cell = enolaListviewGroup.data.get(j).get(i);
                        row.add(Triplet.with(i, 1, cell.toString()));
                    }
                    r += emitRow(row);
                    row.clear();
                }
                return r;
            }
        }

        // orderedby columns
        final ArrayList<String> orderby = new ArrayList<String>();
        for (var col : this.enolaListviewParameters.orderby){
            if (enolaListviewGroup.columnIndex == nameToIndex.get(col.substring(1))) {
                orderby.add(col);
            }
        }
        Collections.sort(enolaListviewGroup.data, (thisRow,thatRow) -> compareTo(0, thisRow, thatRow, orderby));
        for (var i=0; i< enolaListviewGroup.group.size(); i++) {
            row.add(Triplet.with(enolaListviewGroup.columnIndex, enolaListviewGroup.group.get(i).height, enolaListviewGroup.groupKeys.get(i)));
            r += generateGroupByHtml(enolaListviewGroup.group.get(i), row);
        }
        return r;
    }
}


