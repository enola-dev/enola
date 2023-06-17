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
    private String baseUrl;
    private CellData title;
    private HashMap<String, ArrayList<String>> views;

    // index of array is actual displayed column, value is index of column in data
    // so value at index 5, for example 3, means that the content of column 3 in the data is displayed in the 5th column
    public ArrayList<Integer> columnMappings = new ArrayList<Integer>();

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
        var A = a.get(colIndex).value;
        var B = b.get(colIndex).value;
        if (columnDescriptors.get(colIndex).isNumber) {
            if (orderby.get(index).contains("+")){
                return Long.compare(Long.parseLong(A), Long.parseLong(B));
            } else {
                return Long.compare(Long.parseLong(B), Long.parseLong(A));
            }
        } else {
            if (orderby.get(index).contains("+")){
                return A.compareTo(B);
            } else  {
                return B.compareTo(A);
            }
        }
    }

    public EnolaListview(CellData title, HashMap<String, ArrayList<String>> views,  ArrayList<ArrayList<CellData>> data, ArrayList<ColumnDescriptor> columnDescriptors, EnolaListviewParameters enolaListviewParameters){
        this.data = data;
        this.baseUrl = "";
        this.title = title;
        this.views = views;
        this.columnDescriptors = columnDescriptors;
        this.enolaListviewParameters = enolaListviewParameters;
        for (int i=0; i<columnDescriptors.size(); i++) {
            nameToIndex.put(columnDescriptors.get(i).internalName, i);
            System.out.println(columnDescriptors.get(i).internalName);
        }

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
        for (String columnName : enolaListviewParameters.getAllColumns()) {
            System.out.println(columnName + " -> " + nameToIndex.get(columnName));

            columnMappings.add(nameToIndex.get(columnName));
        }
        for (var i=0; i<columnMappings.size(); i++){
             System.out.println(i + " -> " + columnMappings.get(i));
         }

        enolaListviewGroup = new EnolaListviewGroup(this, enolaListviewParameters, data, allColumnIndices, groupbyIndices);
    }

    private int maxRow = 200;
    private int currentCount = 0;

    private String emitRow(ArrayList<Triplet<Integer,Integer, String>> rows){
        var rowByIndex = new HashMap<Integer,Triplet<Integer,Integer, String>>();
        for (var row : rows) {
            rowByIndex.put(row.t, row);
        }
        currentCount++;
        if (currentCount == maxRow) {
            String r= "<tr>";
            for (int i=0; i<columnMappings.size(); i++) {
                // not all three values are always  populated, hence the column index
                if (!rowByIndex.containsKey(columnMappings.get(i))) {
                    continue;
                }
                var row = rowByIndex.get(columnMappings.get(i));
                r += "<td rowspan=\"" + row.u + "\">...</td>";
            }
            r += "</tr>";
            return r;
        }
        if (currentCount > maxRow) {
            return "";
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

        r += "<br>\n";
        r += "<font size=\"8\">";
        r += title.toString();
        r += "</font>";
        r += "<br>\n";

        r += "<font size=\"6\">";
        String query = "";
        for (var view : views.entrySet() ) {
            query = enolaListviewParameters.getWithColumns(view.getValue());
            r += "<a href="+baseUrl+"?" + query + ">  " + view.getKey() +"  <a/>  &nbsp; &nbsp;";
        }
        r += "<br>\n";
        r += "<br>\n";
        r += "</font>";

        r += "<table>\n";
        r += "<tr>";
        for (int j=0; j<columnDescriptors.size(); j++) {
            System.out.println(j + "  " + columnDescriptors.get(j).internalName);
            System.out.println("enolaListviewParameters.getColumns()  " + String.join("|", enolaListviewParameters.getColumns()));
            if (!enolaListviewParameters.getColumns().contains(columnDescriptors.get(j).internalName)) {
                continue;
            }
            System.out.println("columnMappings  " + columnMappings.size());
            var i = columnMappings.get(j);
            String name = columnDescriptors.get(i).internalName;
            r += "<th>";
            // COLUMNS LINKS
            // ORDER BY LINKS
            r += "<font size=\"2\">";
            // query = enolaListviewParameters.getQueryToggledMoveLeft(name);
            // if (query.startsWith("&")) query = query.substring(1);
            // r += "<a href="+baseUrl+"?" + query + ">" + "   &lArr;   " + "<a/>";

            String orderDirectionLabel =
            query = enolaListviewParameters.getQueryToggledOrderbyDirection(name);
            if (query.startsWith("&")) query = query.substring(1);
            r += "<a href="+baseUrl+"?" + query + ">"
                + (enolaListviewParameters.getOrderbyDesc(name)?"&uArr;":"&dArr;")
                + "<a/>";

            //sorting order between columns
            int orderColumnLabel = enolaListviewParameters.getOrderbyColumnOrderIndex(name);
            query = enolaListviewParameters.getQueryToggledOrderbyOrder(name);
            if (query.startsWith("&")) query = query.substring(1);
            r += "<a href="+baseUrl+"?" + query + ">  " + orderColumnLabel +"  <a/>";

            // query = enolaListviewParameters.getQueryToggledMoveRight(name);
            // if (query.startsWith("&")) query = query.substring(1);
            // r += "<a href="+baseUrl+"?" + query + ">" + "&rArr;" + "<a/>";


            // GROUP BY LINKS
            //r += "<br>\n";
            var groupedLabel = enolaListviewParameters.getGroupbyGrouped(name)?"U":"G";
            query = enolaListviewParameters.getQueryToggledGroupby(name);
            if (query.startsWith("&")) query = query.substring(1);
            r += "<a href="+baseUrl+"?" + query + ">" + groupedLabel +"<a/>";

            var groupOrderLabel = enolaListviewParameters.getGroupbyColumnOrderIndex(name);
            if (groupOrderLabel != -1) {
                query = enolaListviewParameters.getQueryToggledGroupbyOrder(name);
                if (query.startsWith("&")) query = query.substring(1);
                r += "<a href="+baseUrl+"?" + query + ">    " + groupOrderLabel +"<a/>";
            }
            r += "<font>";

            r += "<font size=\"4\">";

            r += "<br>\n";
            r += columnDescriptors.get(j).displayName;

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
                    if (!enolaListviewParameters.getColumns().contains(columnDescriptors.get(i).internalName)) {
                        continue;
                    }

                    if (columnDescriptors.get(i).aggregationMethod == AggregationMethod.SUM) {
                        Long acc = 0L;
                        for (int j=0; j < enolaListviewGroup.data.size(); j++) {
                            acc += Long.parseLong(enolaListviewGroup.data.get(j).get(i).value);
                        }
                        row.add(Triplet.with(i, 1, Long.toString(acc)));
                    } else if (columnDescriptors.get(i).aggregationMethod == AggregationMethod.COUNT) {
                        row.add(Triplet.with(i, 1, "("+enolaListviewGroup.data.size()+")"));
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
                        if (!enolaListviewParameters.getColumns().contains(columnDescriptors.get(i).internalName)) {
                            continue;
                        }
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


