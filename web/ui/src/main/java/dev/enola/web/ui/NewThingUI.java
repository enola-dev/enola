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

import dev.enola.thing.proto.ThingOrBuilder;
import dev.enola.thing.proto.Value;
import dev.enola.thing.proto.Value.Link;
import dev.enola.thing.proto.Value.List;
import dev.enola.thing.proto.Value.Literal;

import java.util.Map;

public class NewThingUI {

    // We intentionally don't use any template engine here; see e.g.
    // https://blog.machinezoo.com/template-engines-broken for why.

    // See https://github.com/google/google-java-format/issues/1033 re. STR formatting :()

    // TODO Use Appendable-based approach, for better memory efficiency, and less String "trashing"

    // TODO Rewrite using a Template Engine, e.g. Mustache

    // TODO Implement Escaper as StringTemplate.Processor?
    // (See e.g. https://javaalmanac.io/features/stringtemplates/)

    // TODO rename html() to thing()
    public static CharSequence html(ThingOrBuilder thing) {
        // TODO Print thing.getIri()
        return table(thing.getFieldsMap(), "thing");
    }

    // TODO private?
    // TODO rename html() to value()
    public static CharSequence html(Value value, String tableCssClass) {
        return switch (value.getKindCase()) {
            case STRING -> s(value.getString());
            case LINK -> link(value.getLink());
            case LITERAL -> literal(value.getLiteral());
            case STRUCT -> html(value.getStruct());
            case LIST -> list(value.getList());
            case KIND_NOT_SET -> "";
            default ->
                    throw new IllegalArgumentException("Unexpected value: " + value.getKindCase());
        };
    }

    private static CharSequence literal(Literal literal) {
        // TODO Use a dev.enola.common.convert.Converter based on the literal.getDatatype()
        return literal.getValue();
    }

    private static CharSequence table(Map<String, Value> fieldsMap, String cssClass) {
        // <a href=\"/TODO\">\{s(thingView.getTypeUri())}</a>
        var sb = new StringBuilder("<table");
        if (!cssClass.isEmpty()) {
            sb.append(" class=\"" + s(cssClass) + "\"");
        }
        sb.append("><tbody>\n");
        for (var nameValue : fieldsMap.entrySet()) {
            sb.append("<tr>\n");
            sb.append(STR."<td class=\"label\">\{s(nameValue.getKey())}</td>");
            sb.append(STR."<td>\{html(nameValue.getValue(), "")}</td>");
            sb.append("</tr>\n");
        }
        sb.append("</tbody></table>\n");
        return sb;
    }

    private static CharSequence list(List list) {
        var sb = new StringBuilder();
        sb.append("<ol>\n");
        for (var value : list.getValuesList()) {
            sb.append("<li>");
            sb.append(html(value, ""));
            sb.append("</li>\n");
        }
        sb.append("</ol>\n");
        return sb;
    }

    private static CharSequence link(Link link) {
        var sb = new StringBuilder();
        var iri = link.getIri();
        if (!Strings.isNullOrEmpty(iri)) {
            String url;
            if (iri.startsWith("enola:")) {
                url = "/ui3/" + iri.substring("enola:".length());
            } else {
                url = iri;
            }
            // TODO s(uri) or not - or another escaping?
            sb.append("<a href=" + s(url) + ">");
        }
        sb.append(s(link.getLabel()));
        if (!Strings.isNullOrEmpty(iri)) {
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
