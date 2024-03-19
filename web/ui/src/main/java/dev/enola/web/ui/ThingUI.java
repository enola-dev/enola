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
package dev.enola.web.ui;

import static java.lang.StringTemplate.STR;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;

import dev.enola.core.proto.Thing;
import dev.enola.core.proto.Thing.LinkedText;
import dev.enola.core.proto.ThingOrBuilder;

import java.util.Map;

@Deprecated // TODO To be removed as soon as it's fully replaced with NewThingUI
public class ThingUI {

    public static CharSequence html(ThingOrBuilder value) {
        return html(value, "thing");
    }

    public static CharSequence html(ThingOrBuilder value, String tableCssClass) {
        return switch (value.getKindCase()) {
            case TEXT -> text(value.getText());
            case STRUCT -> table(value.getStruct(), tableCssClass);
            case LIST -> list(value.getList());
            case KIND_NOT_SET -> "";
        };
    }

    private static CharSequence table(Thing.StructOrBuilder thingView, String cssClass) {
        var sb = new StringBuilder("<table");
        if (!cssClass.isEmpty()) {
            sb.append(" class=\"" + s(cssClass) + "\"");
        }
        sb.append("><tbody>\n");
        sb.append(tr(thingView.getFieldsMap()));
        sb.append("</tbody></table>\n");
        return sb;
    }

    private static CharSequence tr(Map<String, Thing> fieldsMap) {
        var sb = new StringBuilder();
        for (var nameValue : fieldsMap.entrySet()) {
            sb.append("<tr>\n");
            sb.append(STR."<td class=\"label\">\{s(nameValue.getKey())}</td>");
            sb.append(STR."<td>\{html(nameValue.getValue(), "")}</td>");
            sb.append("</tr>\n");
        }
        return sb;
    }

    private static CharSequence list(Thing.List list) {
        var sb = new StringBuilder();
        sb.append("<ol>\n");
        for (var value : list.getEntriesList()) {
            sb.append("<li>");
            sb.append(html(value, ""));
            sb.append("</li>\n");
        }
        sb.append("</ol>\n");
        return sb;
    }

    private static CharSequence text(LinkedText text) {
        var sb = new StringBuilder();
        var uri = text.getUri();
        if (!Strings.isNullOrEmpty(uri)) {
            String url;
            if (uri.startsWith("enola:")) {
                url = "/ui/" + uri.substring("enola:".length());
            } else {
                url = uri;
            }
            // TODO s(uri) or not - or another escaping?
            sb.append("<a href=" + s(url) + ">");
        }
        sb.append(s(text.getString()));
        if (!Strings.isNullOrEmpty(uri)) {
            sb.append("</a>");
        }
        return sb;
    }

    private static final Escaper htmlEscaper = HtmlEscapers.htmlEscaper();

    /** Santize raw text to be safe HTML. */
    private static String s(String raw) {
        return htmlEscaper.escape(raw);
    }
}
