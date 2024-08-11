/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.gen.graphviz;

import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.Thing;
import dev.enola.thing.gen.ThingsIntoAppendableConverter;
import dev.enola.thing.repo.StackedThingProvider;
import dev.enola.thing.repo.ThingProvider;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

public class GraphvizGenerator implements ThingsIntoAppendableConverter {

    // NB: RosettaTest#testGraphviz() is the test coverage for this code

    // PS: http://magjac.com/graphviz-visual-editor/ is handy for testing!

    // TODO Lists of Links
    // TODO Link Datatypes, with ports
    // TODO Mouseover full Thing & predicate IRI
    // TODO Thing IRI as direct URL Link
    // TODO wikipedia <https://en.wikipedia.org/wiki/Earth>
    // TODO Shorten long texts, and use e.g. TITLE ?
    // TODO Thing IRI as alternative Link to Enola localhost UI
    // TODO Custom attributes, e.g. Node & Edge color, style etc.
    // TODO Nested blank nodes as Labels, instead just "..."
    // TODO Links from within nested blank nodes with ports?
    // TODO Subgraphs? https://graphviz.org/doc/info/lang.html#subgraphs-and-clusters Classes?
    // TODO Custom attributes at the top graph level, via a http://enola.dev/Graphviz ?
    // TODO compact vs pretty output format

    // FYI: We're intentionally *NOT* showing the Datatype of properties

    private final MetadataProvider metadataProvider;

    public GraphvizGenerator(MetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    @Override
    public boolean convertInto(Iterable<Thing> from, Appendable out)
            throws ConversionException, IOException {
        out.append("digraph {\n");
        try (var ctx = TLC.open()) {
            ctx.push(ThingProvider.class, new StackedThingProvider(from));
            for (Thing thing : from) printThing(thing, out);
        }
        out.append("}\n");
        return true;
    }

    private void printThing(Thing thing, Appendable out) throws IOException {
        out.append("  \"");
        out.append(thing.iri());
        out.append("\" [shape=plain label=<");
        var metadata = metadataProvider.get(thing, thing.iri());
        printNonLinkPropertiesTable(label(metadata), thing, out);
        out.append(">]\n");

        for (var p : thing.predicateIRIs()) {
            if (!thing.isLink(p)) continue;
            out.append("  \"");
            out.append(thing.iri());
            out.append("\" -> \"");
            out.append(thing.getString(p));
            out.append("\" [label=\"");
            out.append(html(label(metadataProvider.get(p))));
            out.append("\"]\n");
        }
        out.append('\n');
    }

    // NB: This is Graphviz and not an HTML table syntax!
    // See https://graphviz.org/doc/info/shapes.html#html
    private void printNonLinkPropertiesTable(
            @Nullable String thingLabel, PredicatesObjects thing, Appendable out)
            throws IOException {
        out.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\">\n");
        if (thingLabel != null) {
            out.append("    <TR><TD COLSPAN=\"2\">");
            out.append(html(thingLabel));
            out.append("</TD></TR>\n");
        }
        for (var p : thing.predicateIRIs()) {
            if (thing.isLink(p)) continue;
            var pLabel = label(metadataProvider.get(p));
            out.append("    <TR><TD ALIGN=\"left\">");
            out.append(html(pLabel));
            out.append("</TD><TD>");
            if (thing.isIterable(p)) {
                var iterable = thing.get(p, Iterable.class);
                if (iterable != null) {
                    for (var element : iterable) {
                        // TODO Convert using datatype; needs thing.get(p, n, String.class)
                        out.append(html(element.toString()));
                        out.append("<BR/>");
                    }
                }
            } else if (thing.isStruct(p)) {
                out.append("...");
            } else {
                var value = thing.getString(p);
                if (value != null) out.append(html(value));
            }
            out.append("</TD></TR>\n");
        }
        out.append("  </TABLE>");
    }

    private static final Escaper htmlEscaper = HtmlEscapers.htmlEscaper();

    private String html(String text) {
        return htmlEscaper.escape(text);
    }
}
