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
import java.util.HashSet;
import static java.util.stream.Collectors.*;

public class EnolaListviewGroup {
    public  ArrayList<ArrayList<CellData>> data;
    public EnolaListview enolaListview;
    public int columnIndex;
    public int size;
    public  ArrayList<String> groupKeys = new ArrayList<String>();
    public  ArrayList<EnolaListviewGroup> group = new ArrayList<EnolaListviewGroup>();
    public HashSet<Integer> remainingColumns;
    private ArrayList<Integer> groupbyIndices;
    public int height;

    public EnolaListviewGroup(EnolaListview enolaListview, EnolaListviewParameters enolaListviewParameters,  ArrayList<ArrayList<CellData>> data, HashSet<Integer> remainingColumns, ArrayList<Integer> groupbyIndices) {
        this.enolaListview = enolaListview;
        this.remainingColumns = remainingColumns;
        this.data = (ArrayList<ArrayList<CellData>>)(data.clone());
        this.groupbyIndices = (ArrayList<Integer>)(groupbyIndices.clone());


        if (groupbyIndices.size() == 0) {
            height = 1;
            return;
        }
        this.columnIndex = groupbyIndices.get(0);
        remainingColumns.remove(this.columnIndex);
        groupbyIndices.remove(0);

        // who needs ordered maps when we have lists? ;-)
        var groupValues = new ArrayList<ArrayList<ArrayList<CellData>>>();
        for (var row : data) {
            var key = row.get(columnIndex).value;
            if (!groupKeys.contains(key)) {
                groupKeys.add(key);
                groupValues.add(new ArrayList<ArrayList<CellData>>());
            }
            var index = groupKeys.indexOf(key);
            groupValues.get(index).add(row);
        }
        height = 0;
        for (var i=0; i<groupKeys.size(); i++){
            var e = new EnolaListviewGroup(enolaListview, enolaListviewParameters, groupValues.get(i), remainingColumns, (ArrayList<Integer>)(groupbyIndices.clone()));
            group.add(e);
            height += e.height;
        }

    }
}


