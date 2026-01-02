/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.thing.KIRI;
import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.Thing;
import dev.enola.thing.gen.Orphanage;
import dev.enola.thing.gen.ThingsIntoAppendableConverter;
import dev.enola.thing.impl.OnlyIRIThing;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.repo.StackedThingProvider;
import dev.enola.thing.repo.ThingProvider;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GraphvizGenerator implements ThingsIntoAppendableConverter {

    // TODO Coalesce e.g. enola:parent & enola:children (schema:inverseOf) into single dir=both link
    // This would be useful e.g. for the TikaMediaTypesThingConverter produced graph diagram
    // Note that strict digraph graphName { concentrate=true does not do this (because of labels;
    // see https://stackoverflow.com/a/3463332/421602).

    // TODO Subgraphs? https://graphviz.org/doc/info/lang.html#subgraphs-and-clusters Classes?

    // TODO Links to other Things (not external HTTP) from within nested blank nodes? With ports??

    private static final int MAX_TEXT_LENGTH = 23;

    public enum Flags implements Context.Key<Boolean> {
        /**
         * In "full" mode, we print a table with properties; in "lite" mode we do not.
         *
         * <p>Note that many of <a href="https://graphviz.org/docs/layouts/">Graphviz's Layout
         * Engines</a> don't seem to work well with full mode.
         */
        FULL
    }

    // NB: RosettaTest#testGraphviz() is the test coverage for this code

    // NB: http://magjac.com/graphviz-visual-editor/ is handy for testing!

    // NB: Because Graphviz (v12) does NOT support rendering A/HREF inside TABLE of LABEL,
    // we cannot make the propertyIRIs clickable, unfortunately. (Nor any object values which are
    // e.g. http://... (of ^schema:URL Datatype).

    // NB: We're intentionally *NOT* showing the Datatype of properties (it's "too much").

    private final ThingMetadataProvider metadataProvider;

    public GraphvizGenerator(ThingMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    @Override
    public boolean convertInto(Iterable<Thing> from, Appendable out)
            throws ConversionException, IOException {
        Set<String> thingIRIs = new HashSet<>();
        var orphanage = new Orphanage();
        out.append("digraph {\n");
        try (var ctx = TLC.open()) {
            ctx.push(ThingProvider.class, new StackedThingProvider(from));
            for (Thing thing : from) {
                orphanage.nonOrphan(thing.iri());
                printThing(thing, out, orphanage);
            }
            for (String orphanIRI : orphanage.orphans()) {
                var orphanThing = new OnlyIRIThing(orphanIRI);
                printThing(orphanThing, out, orphanage);
            }
        }
        out.append("}\n");
        return true;
    }

    private void printThing(Thing thing, Appendable out, Orphanage orphanage) throws IOException {
        boolean full = TLC.optional(Flags.FULL).orElse(false);

        out.append("  \"");
        out.append(thing.iri());
        out.append("\" [");
        if (full) out.append("shape=plain ");

        // Nota bene: This is just an approximate heuristic; if there are multiple types,
        // then we don't know which of them has colors, if any.
        // TODO We could do better and find the first one with a color, if any....
        var types = thing.getThings(KIRI.RDF.TYPE).iterator();
        if (types.hasNext()) {
            printColors(types.next(), out);
        } else {
            printColors(thing, out);
        }

        out.append("URL=\"");
        out.append(thing.iri());
        out.append("\" label=");
        var metadata = metadataProvider.get(thing, thing.iri());
        var label = label(metadata);
        if (full) {
            out.append("<");
            printNonLinkPropertiesTable(label, thing, out);
            out.append(">");
        } else {
            out.append("\"");
            out.append(label);
            out.append("\"");
        }
        out.append("]\n");

        for (var p : thing.predicateIRIs()) {
            for (var link : thing.getLinks(p)) {
                out.append("  \"");
                out.append(thing.iri());
                out.append("\" -> \"");
                var linkIRI = link.toString();
                var linkLabel = label(metadataProvider.get(p));
                out.append(linkIRI);
                out.append("\" [URL=\"");
                out.append(p); // NOT linkIRI
                out.append("\" label=\"");
                out.append(html(linkLabel));
                out.append("\"]\n");
                orphanage.candidate(linkIRI);
            }
        }
        out.append('\n');
    }

    private void printColors(Thing thing, Appendable out) throws IOException {
        var color = thing.get(KIRI.E.COLOR, String.class);
        if (color != null) {
            out.append("style=filled fillcolor=");
            out.append(color);
            out.append(' ');
        }
        var textColor = thing.get(KIRI.E.TEXT_COLOR, String.class);
        if (textColor != null) {
            out.append("fontcolor=");
            out.append(textColor);
            out.append(' ');
        }
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
            if (!thing.getLinks(p).isEmpty()) continue;
            var pLabel = label(metadataProvider.get(p));
            out.append("    <TR><TD ALIGN=\"left\">");
            out.append(html(pLabel));
            out.append("</TD><TD>");
            if (thing.isIterable(p)) {
                var iterable = thing.get(p, Iterable.class);
                if (iterable != null) {
                    for (var element : iterable) {
                        // TODO Convert using datatype; needs thing.get(p, n, String.class)
                        out.append(html(brief(element.toString())));
                        out.append("<BR/>");
                    }
                }
            } else if (thing.isStruct(p)) {
                out.append("..."); // TODO Dig in, or fine as is?
            } else {
                var value = thing.getString(p);
                if (value != null) out.append(html(brief(value)));
            }
            out.append("</TD></TR>\n");
        }
        out.append("  </TABLE>");
    }

    private String brief(String text) {
        var trim = text.trim();
        if (trim.length() > MAX_TEXT_LENGTH) return trim.substring(0, MAX_TEXT_LENGTH) + "...";
        else return trim;
    }

    private static final Escaper htmlEscaper = HtmlEscapers.htmlEscaper();

    private String html(String text) {
        return htmlEscaper.escape(text);
    }
}
