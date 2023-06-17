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

public class CellData {
    public String value;
    public boolean isNumber = false;
    public String url;
    public String extra;
    public String linkLabel;
    public String link;

    public CellData(String value) {
        this.value = value;
    }
    public CellData(String value, String url) {
        this.value = value;
        this.url = url;
    }
    public CellData(String value, String url, String extra, String linkLabel, String link, boolean isNumber) {
        this.value = value;
        this.url = url;
        this.extra = extra;
        this.linkLabel = linkLabel;
        this.link = link;
        this.isNumber = isNumber;
    }


    public String toString() {
        var hasExtraLink = link != null && linkLabel != null && !link.isEmpty() && !linkLabel.isEmpty();
        var e = "";
        if (extra != null) {
            e = extra;
        }
        var href = value + " " + e;
        if (url != null ) {
            href = "<a href=\"" + url + "\">" + value + "</a>" + "&nbsp;" + e;
        }
        var link2  = "";
        if (hasExtraLink) {
            link2 = "<font size=\"4\"><a href=\"" + link + "\">" + linkLabel + "</a></font>";
        }
        return href + "&nbsp;" + link2;
    }

    public void Dump(){
        System.out.println("CellData");
        System.out.println("   value:" + value);
        System.out.println("   url  :" + url);
        System.out.println("   extra:" + extra);
        System.out.println("   linkLabel:" + linkLabel);
        System.out.println("   link:" + link);
    }
}
