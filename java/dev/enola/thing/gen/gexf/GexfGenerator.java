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
package dev.enola.thing.gen.gexf;

import com.google.common.collect.Iterables;
import com.google.common.escape.Escaper;
import com.google.common.xml.XmlEscapers;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.time.Interval;
import dev.enola.common.xml.TemporalToXmlStringConverter;
import dev.enola.thing.Thing;
import dev.enola.thing.gen.ThingsIntoAppendableConverter;
import dev.enola.thing.metadata.ThingHierarchyProvider;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.metadata.ThingTimeProvider;
import dev.enola.thing.repo.StackedThingProvider;
import dev.enola.thing.repo.ThingProvider;

import java.io.IOException;

public class GexfGenerator implements ThingsIntoAppendableConverter {

    // TODO Option to skip writing pid= and <parents>

    // TODO Write Attributes? https://gexf.net/data.html
    //   - Does this clutter the graph? Or not... too much, all, really?
    //   - Can it be used as Hierarchy Attribute for Circle Layout on https://gephi.org/gephi-lite/?

    // TODO Treat blank nodes same as in GraphvizGenerator

    // TODO Colors, see https://gexf.net/viz.html
    //   - Custom Node color, shape & size; re-using logic from GraphvizGenerator
    //   - Custom Edge color, thickness, shape; re-using logic from GraphvizGenerator

    // NB: RosettaTest#testGraphviz() is the test coverage for this code

    private final ThingMetadataProvider metadataProvider;
    private final ThingTimeProvider timeProvider = new ThingTimeProvider();
    private final ThingHierarchyProvider hierarchyProvider = new ThingHierarchyProvider();
    private final TemporalToXmlStringConverter xmlConverter =
            new TemporalToXmlStringConverter(false);

    public GexfGenerator(ThingMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    @Override
    public boolean convertInto(Iterable<Thing> from, Appendable out)
            throws ConversionException, IOException {
        out.append(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<gexf xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns=\"http://gexf.net/1.3\""
                        + " xsi:schemaLocation=\"http://gexf.net/1.3 http://gexf.net/1.3/gexf.xsd\""
                        + " version=\"1.3\">\n");
        out.append("  <meta><creator>Enola.dev</creator></meta>\n");
        out.append(
                "  <graph defaultedgetype=\"directed\" mode=\"dynamic\" timeformat=\"dateTime\""
                        + " timerepresentation=\"interval\">\n");

        try (var ctx = TLC.open()) {
            ctx.push(ThingProvider.class, new StackedThingProvider(from));

            out.append("    <nodes>\n");
            for (Thing thing : from) printThingNode(thing, out);
            out.append("    </nodes>\n");

            out.append("    <edges>\n");
            for (Thing thing : from) printThingEdges(thing, out);
            out.append("    </edges>\n");
        }

        out.append("  </graph>\n</gexf>\n");
        return true;
    }

    private void printThingNode(Thing thing, Appendable out) throws IOException {
        var id = thing.iri();
        var metadata = metadataProvider.get(thing, thing.iri());

        out.append("      <node id=\"");
        xmlAttribute(id, out);
        out.append("\" label=\"");
        xmlAttribute(label(metadata), out);
        out.append("\"");
        boolean mustClose = true;

        var intervals = timeProvider.existance(thing);
        if (!Iterables.isEmpty(intervals)) {
            if (Iterables.size(intervals) == 1) {
                var interval = intervals.iterator().next();
                printInterval(interval, out);

            } else {
                out.append(">\n");
                mustClose = false;
                out.append("        <spells>\n");
                for (var interval : intervals) {
                    out.append("          <spell");
                    printInterval(interval, out);
                }
                out.append("        </spells>\n");
            }
        }

        var parents = hierarchyProvider.parents(thing);
        if (!Iterables.isEmpty(parents)) {
            if (mustClose && Iterables.size(parents) == 1) {
                var parent = parents.iterator().next();
                out.append(" pid=\"");
                xmlAttribute(parent, out);
                out.append("\"");

            } else {
                out.append(">\n");
                mustClose = false;
                out.append("        <parents>\n");
                for (var parent : parents) {
                    out.append("          <parent for=\"");
                    xmlAttribute(parent, out);
                    out.append("\"/>\n");
                }
                out.append("        </parents>\n");
            }
        }

        if (mustClose) out.append("/>\n");
        else out.append("      </node>\n");
    }

    private void printInterval(Interval interval, Appendable out) throws IOException {
        if (!interval.isUnboundedStart()) {
            out.append(" start=\"");
            xmlAttribute(xmlConverter.convert(interval.start()), out);
            out.append("\"");
        }
        if (!interval.isUnboundedEnd()) {
            out.append(" end=\"");
            xmlAttribute(xmlConverter.convert(interval.end()), out);
            out.append("\"");
        }
    }

    private void printThingEdges(Thing thing, Appendable out) throws IOException {
        for (var linkPropertyIRI : thing.predicateIRIs()) {
            for (var linkIRI : thing.getLinks(linkPropertyIRI)) {
                var linkMetadata = metadataProvider.get(linkPropertyIRI);

                var source = thing.iri();
                var target = linkIRI.toString();
                var kind = linkPropertyIRI;
                var label = label(linkMetadata);

                out.append("      <edge source=\"");
                xmlAttribute(source, out);
                out.append("\" target=\"");
                xmlAttribute(target, out);
                out.append("\" kind=\"");
                xmlAttribute(kind, out);
                out.append("\" label=\"");
                xmlAttribute(label, out);
                out.append("\"/>\n");
            }
        }
    }

    private void xmlAttribute(String text, Appendable out) throws IOException {
        out.append(XML_ATTRIBUTE_ESCAPER.escape(text));
    }

    private static final Escaper XML_ATTRIBUTE_ESCAPER = XmlEscapers.xmlAttributeEscaper();
}
