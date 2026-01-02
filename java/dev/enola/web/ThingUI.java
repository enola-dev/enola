/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.web;

import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;

import dev.enola.thing.gen.LinkTransformer;
import dev.enola.thing.message.ProtoThingMetadataProvider;
import dev.enola.thing.proto.ThingOrBuilder;
import dev.enola.thing.proto.Value;
import dev.enola.thing.proto.Value.List;
import dev.enola.thing.proto.Value.Literal;

import java.util.Map;

public class ThingUI {

    // See https://github.com/google/google-java-format/issues/1033 re. using moar STR formatting ;(

    // We intentionally don't use any (other) template engine here; see e.g.
    // https://blog.machinezoo.com/template-engines-broken for why.
    // TODO Or rewrite using e.g. Mustache? Client Side? ;)

    // TODO Use Appendable-based approach, for better memory efficiency, and less String "trashing"

    private final ProtoThingMetadataProvider metadataProvider;
    private final LinkTransformer linkTransformer;

    public ThingUI(ProtoThingMetadataProvider metadataProvider, LinkTransformer linkTransformer) {
        this.metadataProvider = metadataProvider;
        this.linkTransformer = linkTransformer;
    }

    public CharSequence html(ThingOrBuilder thing) {
        // TODO Print/include thing.getIri() on of HTML, but with initial / link
        return table(thing.getPropertiesMap(), "thing");
    }

    private CharSequence value(Value value, String tableCssClass) {
        return switch (value.getKindCase()) {
            case STRING -> s(value.getString());
            case LANG_STRING ->
                    s(value.getLangString().getText() + "@" + value.getLangString().getLang());
            case LINK -> link(value.getLink());
            case LITERAL -> literal(value.getLiteral());
            case STRUCT -> table(value.getStruct().getPropertiesMap(), tableCssClass);
            case LIST -> list(value.getList());
            case KIND_NOT_SET -> "";
            default ->
                    throw new IllegalArgumentException("Unexpected value: " + value.getKindCase());
        };
    }

    private CharSequence literal(Literal literal) {
        // TODO Use a dev.enola.common.convert.Converter based on the literal.getDatatype()
        return "<span title=" + literal.getDatatype() + ">" + s(literal.getValue()) + "</span>";
    }

    private CharSequence table(Map<String, Value> fieldsMap, String cssClass) {
        var sb = new StringBuilder("<table");
        if (!cssClass.isEmpty()) {
            sb.append(" class=\"").append(s(cssClass)).append("\"");
        }
        sb.append("><tbody>\n");
        for (var nameValue : fieldsMap.entrySet()) {
            sb.append("<tr>\n");
            sb.append("<td class=\"label\">").append(link(nameValue.getKey())).append("</td>");
            sb.append("<td>").append(value(nameValue.getValue(), "")).append("</td>");
            sb.append("</tr>\n");
        }
        sb.append("</tbody></table>\n");
        return sb;
    }

    private CharSequence list(List list) {
        var sb = new StringBuilder();
        sb.append("<ol>\n");
        for (var value : list.getValuesList()) {
            sb.append("<li>");
            sb.append(value(value, ""));
            sb.append("</li>\n");
        }
        sb.append("</ol>\n");
        return sb;
    }

    private CharSequence link(String iri) {
        var meta = metadataProvider.get(iri);
        var sb = new StringBuilder();
        sb.append(meta.imageHTML());
        sb.append(' ');
        // TODO s(uri) or not - or another escaping?
        sb.append("<a href=" + s(linkTransformer.get(iri)));
        var description = meta.descriptionHTML();
        if (!description.isEmpty()) {
            sb.append(" title=\"");
            sb.append(s(description));
            sb.append('"');
        }
        sb.append('>');
        sb.append(s(meta.label()));
        sb.append("</a>");
        return sb;
    }

    private static final Escaper htmlEscaper = HtmlEscapers.htmlEscaper();

    /** Santize raw text to be safe HTML. */
    // TODO Implement Escaper as StringTemplate.Processor?
    // (See e.g. https://javaalmanac.io/features/stringtemplates/)
    private static String s(String raw) {
        // Replacement of "#" by "%23" is required so that links such as e.g
        // http://[::]:8080/ui/http://www.w3.org/1999/02/22-rdf-syntax-ns#subject
        // et al. work as needed!
        return htmlEscaper.escape(raw).replace("#", "%23");
    }
}
